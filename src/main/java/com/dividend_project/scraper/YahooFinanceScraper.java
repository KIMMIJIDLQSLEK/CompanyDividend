package com.dividend_project.scraper;

import com.dividend_project.model.Company;
import com.dividend_project.model.Dividend;
import com.dividend_project.model.ScrapedResult;
import com.dividend_project.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper{
    private static final String STATISTICS_URL="https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_UTL="https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME=86400; // 60초*60분*24시간


    @Override
    public ScrapedResult scrap(Company company){
        var scrapResult=new ScrapedResult();
        scrapResult.setCompany(company); //회사입력
        try {
            long now=System.currentTimeMillis();  //1970-01-01 부터 현재까지의 시간
            String url=String.format(STATISTICS_URL,company.getTicker(),START_TIME,now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableEle = parsingDivs.get(0);

            Element tbody = tableEle.children().get(1); //table의 thead(0), tbody(1), tfoot(2)에서 배당금은 1에 해당
            /*
            <td class="Ta(start) Py(10px)" colspan="6"><strong>3.5</strong> <span>Dividend</span></td>
            <td class="Ta(start) Py(10px)" colspan="6"><strong>0.25</strong> <span>Dividend</span></td>
             */
            List<Dividend> dividends=new ArrayList<>();
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }
                String[] splits = txt.split(" ");
                int month = Month.strToNum(splits[0]);
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if(month<0){
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                }

                dividends.add(
                        Dividend.builder()
                        .date(LocalDateTime.of(year,month,day,0,0))
                        .dividend(dividend).build()
                );
            }
            scrapResult.setDividendEntities(dividends); //회사의 배당금 list들을 입력


        } catch (IOException e) {
            e.printStackTrace();
        }

        return scrapResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker){
        String url=String.format(SUMMARY_UTL,ticker,ticker);


        try{
            Document document=Jsoup.connect(url).get();
            Element titleEle=document.getElementsByTag("h1").get(0);
            String title=titleEle.text().split(" - ")[1].trim();


            return Company.builder()
                    .ticker(ticker)
                    .name(title)
                    .build();

        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

}
