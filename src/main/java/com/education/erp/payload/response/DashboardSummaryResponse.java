package com.education.erp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DashboardSummaryResponse {
    private long students;
    private long teachers;
    private long classes;
    private long assignments;
    private long submissions;
    private long notifications;
    private List<String> recentLogs;
}