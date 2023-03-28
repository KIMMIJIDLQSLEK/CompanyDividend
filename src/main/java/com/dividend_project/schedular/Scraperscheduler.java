package com.dividend_project.schedular;

import com.dividend_project.model.Company;
import com.dividend_project.model.ScrapedResult;
import com.dividend_project.persist.CompanyRepository;
import com.dividend_project.persist.DividendRepository;
import com.dividend_project.persist.entitiy.CompanyEntity;
import com.dividend_project.persist.entitiy.DividendEntity;
import com.dividend_project.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class Scraperscheduler {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;

    //일정 주기마다 수행
    @Scheduled(cron="${scheduler.scrap.yahoo}") //매일 정각에
    public void yahooFinanceScheduling(){
        log.info("scraping scheduler is started");

        //저장된 회사 목록을 조회
        List<CompanyEntity> companies=this.companyRepository.findAll();

        //회사마다 배당금 정보를 새로 스크래핑
        for(var company:companies){
            log.info("scraping scheduler is started -> "+company.getName());
            ScrapedResult scrapedResult= this.yahooFinanceScraper.scrap(Company.builder()
                    .name(company.getName())
                    .ticker(company.getTicker())
                    .build());

            //스크래핑한 배당금 정보 중 데이터베이스에 없는 값은 저장
            scrapedResult.getDividendEntities().stream()
                    .map(e-> new DividendEntity(company.getId(),e))
                    .forEach(e->{
                        boolean exists=this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(),e.getDate());
                        if(!exists){
                            this.dividendRepository.save(e);
                        }
                    });

            //연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            try {
                Thread.sleep(3000);// 3초 일시정지
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }


}
