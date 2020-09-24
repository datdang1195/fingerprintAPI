package com.ekino.team2.punctuality.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Objects;

@Data
@Builder
@Document(collection = "UserStatus")
public class UserStatus {
    @Id
    private Long id;
    private String userCode;
    private String userName;
    private float workingDay;
    private String status;
    private String description;
    private LocalDate recordDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserStatus that = (UserStatus) o;
        return Objects.equals(userCode, that.userCode) &&
                Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userCode, userName);
    }
}
