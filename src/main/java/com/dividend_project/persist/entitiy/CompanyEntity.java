package com.dividend_project.persist.entitiy;

import com.dividend_project.model.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity(name= "COMPANY")
@Getter
@ToString
@NoArgsConstructor
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String ticker;


    public CompanyEntity(Company company){
        this.name= company.getName();
        this.ticker= company.getTicker();
    }

}
