package com.academic.performance.entity;

/** What kind of data triggered an early-warning alert. */
public enum AlertType {
    ATTENDANCE,  // attendance below the required threshold
    MARKS,       // marks are declining
    PREDICTION   // predicted performance is poor
}
