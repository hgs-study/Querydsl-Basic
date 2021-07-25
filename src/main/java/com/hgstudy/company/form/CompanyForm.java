package com.hgstudy.company.form;

import com.querydsl.core.annotations.QueryProjection;

public class CompanyForm {
    public static class Response{

        public static class Find{

            private Long id;
            private String name;
            private String department;

            @QueryProjection
            public Find(String name, String department) {
                this.name = name;
                this.department = department;
            }
        }
    }
}
