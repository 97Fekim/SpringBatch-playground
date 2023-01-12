package com.psc.sample.q103.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Setter
@Getter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Dept2 {

    @Id
    Integer deptNo; // 부서번호
    String dName;   // 부서이름
    String loc;     // 위치(location)

}
