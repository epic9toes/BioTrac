package com.lloydant.biotrac.Repositories.implementations;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.lloydant.biotrac.ApolloConnector;
import com.lloydant.biotrac.GetCoursemateQuery;
import com.lloydant.biotrac.GetDepartmentalStudentForEnrollmentQuery;
import com.lloydant.biotrac.GetStudentsByDepartmentQuery;
import com.lloydant.biotrac.Repositories.IStudentSearchRepository;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class StudentSearchRepo implements IStudentSearchRepository {

    @Override
    public Observable<Response<GetDepartmentalStudentForEnrollmentQuery.Data>> GetStudentsByDepartment(String department,
                                                                                                       int level, String token) {
        ApolloCall<GetDepartmentalStudentForEnrollmentQuery.Data> apolloCall = ApolloConnector.setupApollo(token)
                .query(new GetDepartmentalStudentForEnrollmentQuery(department,level));

        return Rx2Apollo.from(apolloCall).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


}
