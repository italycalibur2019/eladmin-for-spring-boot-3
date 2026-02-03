/*
 *  Copyright 2019-2025 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.italycalibur.ciallo.domain.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ZipUtil;
import com.italycalibur.ciallo.domain.entity.ColumnInfo;
import com.italycalibur.ciallo.domain.entity.GenConfig;
import com.italycalibur.ciallo.domain.exception.BadRequestException;
import com.italycalibur.ciallo.domain.repository.ColumnInfoRepository;
import com.italycalibur.ciallo.domain.service.GeneratorService;
import com.italycalibur.ciallo.domain.utils.*;
import com.italycalibur.ciallo.domain.vo.TableInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Zheng Jie
 * @date 2019-01-02
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings({"unchecked","all"})
public class GeneratorServiceImpl implements GeneratorService {
    private static final Logger log = LoggerFactory.getLogger(GeneratorServiceImpl.class);

    private final ColumnInfoRepository columnInfoRepository;

    private final String CONFIG_MESSAGE = "请先配置生成器";
    @Override
    public Object getTables() {
        // 使用预编译防止sql注入
        String sql = "select table_schema as schema_name, table_name, cast(null as timestamp) as create_time, '-' as engine, '-' as coding, " +
                "obj_description(cast(quote_ident(table_schema) || '.' || quote_ident(table_name) as regclass), 'pg_class') as remark " +
                "from information_schema.tables " +
                "where table_schema not in ('pg_catalog', 'information_schema') " +
                "order by table_schema desc";
        return columnInfoRepository.nativeQueryForVo(TableInfo.class, sql);
    }

    @Override
    public PageResult<TableInfo> getTables(String name, int[] startEnd) {
        // 使用预编译防止sql注入
        String sql = "select table_schema as schema_name, table_name, cast(null as timestamp) as create_time, '-' as engine, '-' as coding, " +
                "obj_description(cast(quote_ident(table_schema) || '.' || quote_ident(table_name) as regclass), 'pg_class') as remark " +
                "from information_schema.tables " +
                "where table_schema not in ('pg_catalog', 'information_schema') and table_name like ? " +
                "order by table_schema desc";
        String countSql = "select count(1) from information_schema.tables " +
                "where table_schema not in ('pg_catalog', 'information_schema') and table_name like ?";
        // 这里不使用hutool参数转换的分页，改用jpa分页，第一个参数是页码，第二个参数是每页条数
        Pageable pageable = PageRequest.of(startEnd[0], startEnd[1]);
        Page<TableInfo> page = columnInfoRepository.nativeQueryForVo(TableInfo.class, countSql, sql, pageable, StringUtils.isNotBlank(name) ? ("%" + name + "%") : "%%");
        return PageUtil.toPage(page);
    }

    @Override
    public List<ColumnInfo> getColumns(String tableName) {
        List<ColumnInfo> columnInfos = columnInfoRepository.findByTableNameOrderByIdAsc(tableName);
        if (CollectionUtil.isNotEmpty(columnInfos)) {
            return columnInfos;
        } else {
            columnInfos = query(tableName);
            return columnInfoRepository.saveAll(columnInfos);
        }
    }

    @Override
    public List<ColumnInfo> query(String tableName) {
        // 使用预编译防止sql注入
        String sql = "SELECT" +
                "    t.relname AS table_name," +
                "    c.column_name," +
                "    c.data_type AS column_type," +
                "    c.is_nullable," +
                "    d.description AS remark," +
                "    c.ordinal_position " +
                "FROM pg_stat_user_tables t " +
                "JOIN information_schema.columns c ON c.table_name = t.relname AND c.table_schema = t.schemaname " +
                "LEFT JOIN pg_description d ON d.objoid = t.relid AND d.objsubid = c.ordinal_position " +
                "WHERE t.relname = ? AND c.table_schema NOT IN ('pg_catalog', 'information_schema') " +
                "ORDER BY c.ordinal_position";

        List<Object[]> result = columnInfoRepository.nativeQuery(sql, tableName);
        List<ColumnInfo> columnInfos = new ArrayList<>();
        for (Object[] arr : result) {
            columnInfos.add(
                    new ColumnInfo(
                            tableName,
                            arr[2].toString(),
                            "NO".equals(arr[1]),
                            arr[4].toString(),
                            arr[3] == null ? null : arr[3].toString(),
                            null,
                            null)
            );
        }
        return columnInfos;
    }

    @Override
    public void sync(List<ColumnInfo> columnInfos, List<ColumnInfo> columnInfoList) {
        // 第一种情况，数据库类字段改变或者新增字段
        for (ColumnInfo columnInfo : columnInfoList) {
            // 根据字段名称查找
            List<ColumnInfo> columns = columnInfos.stream().filter(c -> c.getColumnName().equals(columnInfo.getColumnName())).collect(Collectors.toList());
            // 如果能找到，就修改部分可能被字段
            if (CollectionUtil.isNotEmpty(columns)) {
                ColumnInfo column = columns.get(0);
                column.setColumnType(columnInfo.getColumnType());
                column.setExtra(columnInfo.getExtra());
                column.setKeyType(columnInfo.getKeyType());
                if (StringUtils.isBlank(column.getRemark())) {
                    column.setRemark(columnInfo.getRemark());
                }
                columnInfoRepository.save(column);
            } else {
                // 如果找不到，则保存新字段信息
                columnInfoRepository.save(columnInfo);
            }
        }
        // 第二种情况，数据库字段删除了
        for (ColumnInfo columnInfo : columnInfos) {
            // 根据字段名称查找
            List<ColumnInfo> columns = columnInfoList.stream().filter(c -> c.getColumnName().equals(columnInfo.getColumnName())).collect(Collectors.toList());
            // 如果找不到，就代表字段被删除了，则需要删除该字段
            if (CollectionUtil.isEmpty(columns)) {
                columnInfoRepository.delete(columnInfo);
            }
        }
    }

    @Override
    public void save(List<ColumnInfo> columnInfos) {
        columnInfoRepository.saveAll(columnInfos);
    }

    @Override
    public void generator(GenConfig genConfig, List<ColumnInfo> columns) {
        if (genConfig.getId() == null) {
            throw new BadRequestException(CONFIG_MESSAGE);
        }
        try {
            GenUtil.generatorCode(columns, genConfig);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException("生成失败，请手动处理已生成的文件");
        }
    }

    @Override
    public ResponseEntity<Object> preview(GenConfig genConfig, List<ColumnInfo> columns) {
        if (genConfig.getId() == null) {
            throw new BadRequestException(CONFIG_MESSAGE);
        }
        List<Map<String, Object>> genList = GenUtil.preview(columns, genConfig);
        return new ResponseEntity<>(genList, HttpStatus.OK);
    }

    @Override
    public void download(GenConfig genConfig, List<ColumnInfo> columns, HttpServletRequest request, HttpServletResponse response) {
        if (genConfig.getId() == null) {
            throw new BadRequestException(CONFIG_MESSAGE);
        }
        try {
            File file = new File(GenUtil.download(columns, genConfig));
            String zipPath = file.getPath() + ".zip";
            ZipUtil.zip(file.getPath(), zipPath);
            FileUtil.downloadFile(request, response, new File(zipPath), true);
        } catch (IOException e) {
            throw new BadRequestException("打包失败");
        }
    }
}
