package domain.entity;

import domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Task {

    private Integer id;

    private String name;

    private Status status;

}