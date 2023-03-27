package com.dividend_project.service;

import com.dividend_project.model.Company;
import com.dividend_project.model.ScrapedResult;
import com.dividend_project.persist.CompanyRepository;
import com.dividend_project.persist.DividendRepository;
import com.dividend_project.persist.entitiy.CompanyEntity;
import com.dividend_project.persist.entitiy.DividendEntity;
import com.dividend_project.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final Scraper YahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker); //ticker에 해당하는 회사의 존재여부

        //이미 db에 있다면
        if (exists) {
            throw new RuntimeException("already exists ticker -> " + ticker);
        }
        //db에 없다면 저장
        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {
        //todo: ticker를 기준으로 회사를 스크래핑
        Company company = this.YahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        //todo: 해당 회사가 존재할 경우, 회사의 배당금을 스크래핑
        ScrapedResult scrapedResult = this.YahooFinanceScraper.scrap(company);

        //todo: 스크래핑 결과를 저장
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntityList =
                scrapedResult.getDividendEntities().stream()
                        .map(e -> new DividendEntity(companyEntity.getId(), e))
                        .collect(Collectors.toList());

        this.dividendRepository.saveAll(dividendEntityList);
        return company;
    }

    //TODO: 자동완성 2
    //추가
    public void addAutocompleteKeyword(String keyword){
        this.trie.put(keyword,null);
    }

    //조회
    public List<String> autocomplete(String keyword){
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    }

    //삭제
    public void deleteAutocompleteKeyword(String keyword){
        this.trie.remove(keyword);
    }

    //TODO: 자동완성 3
    public List<String> getCompanyNamesByKeyword(String keyword){
        Pageable limit= PageRequest.of(0,10);
        Page<CompanyEntity> companyEntities=this.companyRepository.findByNameStartingWithIgnoreCase(keyword,limit);
        return companyEntities.stream()
                .map(e->e.getName())
                .collect(Collectors.toList());
    }




}
