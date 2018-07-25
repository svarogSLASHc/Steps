package com.test.pedometer.domain.fileaccess;

import android.content.Context;

import com.test.pedometer.data.fileaccess.LoggerRepositoryFileImpl;

public abstract class AbstractLoggerController {
    protected LoggerRepository loggerRepository;

    public AbstractLoggerController(Context context) {
        loggerRepository = LoggerRepositoryFileImpl.newInstance(context, getFileName());
    }

    public void add(String data) {
        loggerRepository.add(data);
    }

    public void save(String data) {
        loggerRepository.save(data);
    }

    public String saveLog() {
        return loggerRepository.saveAll();
    }

    public String getLogFileAsString() {
        return loggerRepository.getRawText();
    }

    public void clear() {
        loggerRepository.clear();
    }

    protected abstract String getFileName();
}
