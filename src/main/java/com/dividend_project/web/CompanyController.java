package com.dividend_project.web;

import com.dividend_project.model.Company;
import com.dividend_project.persist.entitiy.CompanyEntity;
import com.dividend_project.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/autocomplete") //회사 배당금 검색-자동완성
    public ResponseEntity<?> autocomplete(@RequestParam String keyword){ //배당금 검색-자동완성
        //todo: 자동완성 2
//        var result =this.companyService.autocomplete(keyword);

        //todo: 자동완성 3
        var result=this.companyService.getCompanyNamesByKeyword(keyword);

        return ResponseEntity.ok(result);
    }

    @GetMapping  //회사 리스트 조회
    public ResponseEntity<?> searchCompany(final Pageable pageable){ //회사리스트 조회
        Page<CompanyEntity> companies= this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companies);

    }

    @PostMapping //회사 배당금 저장
    public ResponseEntity<?> addCompany(@RequestBody Company request){ //회사 배당금 저장
        String ticker=request.getTicker().trim();
        if(ObjectUtils.isEmpty(ticker)){
            throw new RuntimeException("ticker is empty");
        }

        Company company=this.companyService.save(ticker);
        this.companyService.addAutocompleteKeyword(company.getName());
        return ResponseEntity.ok(company);
    }

    @DeleteMapping//회사 배당금 삭제
    public void deleteCompany(@RequestParam String ticker){
        return;
    }
}
