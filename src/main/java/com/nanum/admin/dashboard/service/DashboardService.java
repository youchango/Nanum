package com.nanum.admin.dashboard.service;

import com.nanum.admin.dashboard.dto.DashboardDTO;

public interface DashboardService {
    DashboardDTO getDashboardSummary(String siteCd);
}
