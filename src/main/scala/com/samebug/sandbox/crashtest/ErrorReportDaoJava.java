package com.samebug.sandbox.crashtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.casbah.MongoDB;
import org.mongojack.JacksonDBCollection;

import javax.inject.Inject;
import java.util.List;


class ErrorReportDaoJava {
    private JacksonDBCollection<ErrorReport, String> collection;

    @Inject
    public ErrorReportDaoJava(MongoDB mongodb, ObjectMapper mapper) {
        this.collection =
                JacksonDBCollection.wrap(
                        mongodb.getCollection("errors_reports"),
                        ErrorReport.class, String.class,
                        mapper);
    }

    ErrorReport insert(ErrorReport report) {
        return collection.insert(report).getSavedObject();
    }

    List<ErrorReport> insert(List<ErrorReport> reports) {
        return collection.insert(reports).getSavedObjects();
    }
}
