package com.dividend_project.scraper;

import com.dividend_project.model.Company;
import com.dividend_project.model.ScrapedResult;


//확장성 용이
public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
