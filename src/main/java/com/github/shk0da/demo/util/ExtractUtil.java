package com.github.shk0da.demo.util;

import com.github.shk0da.demo.exception.DemoException;
import com.github.shk0da.demo.exception.ErrorCode;
import com.github.shk0da.demo.model.contacts.SortField;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

import java.util.List;

@UtilityClass
public class ExtractUtil {

    public static List<Long> extractGroupIds(List<String> groupIds) {
        List<Long> ids = Lists.newArrayList();
        if (groupIds != null && !groupIds.isEmpty()) {
            for (int i = 0; i < groupIds.size(); i++) {
                try {
                    ids.add(Long.valueOf(groupIds.get(i)));
                } catch (Exception ex) {
                    throw new DemoException(ErrorCode.CODE_101, ImmutableMap.of(ErrorCode.Constants.PARAMETER_NAME, "groupIds[" + i + "]"));
                }
            }
        }
        return ids;
    }

    public static Long extractGroupId(String groupId) {
        try {
            return Long.valueOf(groupId);
        } catch (Exception ex) {
            throw new DemoException(ErrorCode.CODE_101, ImmutableMap.of(ErrorCode.Constants.PARAMETER_NAME, "groupId"));
        }
    }

    public static Long extractContactId(String contactId) {
        try {
            return Long.valueOf(contactId);
        } catch (Exception ex) {
            throw new DemoException(ErrorCode.CODE_101, ImmutableMap.of(ErrorCode.Constants.PARAMETER_NAME, "contactId"));
        }
    }

    public static SortField extractSortField(String sortField) {
        if (sortField == null) return null;
        try {
            return SortField.valueOf(sortField);
        } catch (Exception ex) {
            throw new DemoException(ErrorCode.CODE_101, ImmutableMap.of(ErrorCode.Constants.PARAMETER_NAME, "sortBy"));
        }
    }

    public static Sort.Direction extractSortDirection(String sortDirection) {
        if (sortDirection == null) return null;
        try {
            return Sort.Direction.valueOf(sortDirection);
        } catch (Exception ex) {
            throw new DemoException(ErrorCode.CODE_101, ImmutableMap.of(ErrorCode.Constants.PARAMETER_NAME, "sortDirection"));
        }
    }
}
