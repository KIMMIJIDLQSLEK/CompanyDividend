package com.dividend_project.service;


import com.dividend_project.model.Dividend;
import com.dividend_project.model.Company;
import com.dividend_project.model.ScrapedResult;
import com.dividend_project.persist.CompanyRepository;
import com.dividend_project.persist.DividendRepository;
import com.dividend_project.persist.entitiy.CompanyEntity;
import com.dividend_project.persist.entitiy.DividendEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.UTFDataFormatException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName){
        //todo: 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company=
                this.companyRepository.findByName(companyName)
                        .orElseThrow(()-> new RuntimeException("존재하지 않는 회사명입니다.")); //에러날경우 예외처리, 값이 있을 경우 companyEntity를 가져옴

        //todo: 2. 조회된 회사 id로 배당금 정보를 조회
        List<DividendEntity> dividendEntities=this.dividendRepository.findAllByCompanyId(company.getId());

        //todo: 3. 결과 조합 후 반환
        List<Dividend> dividends=new ArrayList<>();

        for(var entity:dividendEntities){
            dividends.add(Dividend.builder()
                    .date(entity.getDate())
                    .dividend(entity.getDividend())
                    .build());
        }

        return new ScrapedResult(
                Company.builder()
                        .ticker(company.getTicker())
                        .name(company.getName())
                        .build()
                ,dividends);
    }


}
