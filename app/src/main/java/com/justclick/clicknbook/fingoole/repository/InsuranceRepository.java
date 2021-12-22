package com.justclick.clicknbook.fingoole.repository;

public class InsuranceRepository {
    private static InsuranceRepository insuranceRepositoryInstance;

    public InsuranceRepository getInsuranceRepositoryInstance(){
        if(insuranceRepositoryInstance==null){
            insuranceRepositoryInstance= new InsuranceRepository();
        }
        return insuranceRepositoryInstance;
    }

}
