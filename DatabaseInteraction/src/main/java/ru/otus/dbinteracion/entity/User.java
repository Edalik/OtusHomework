package ru.otus.dbinteracion.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.otus.dbinteracion.annotation.RepositoryField;
import ru.otus.dbinteracion.annotation.RepositoryIdField;
import ru.otus.dbinteracion.annotation.RepositoryTable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RepositoryTable("users")
public class User {

    @RepositoryIdField("id")
    private Integer id;

    @RepositoryField("login")
    private String test;

    @RepositoryField("password")
    private String password;

    @RepositoryField("nickname")
    private String nickname;

}