package com.novoda.downloadmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class DownloadBatchTest {

    private static final DownloadFile DOWNLOAD_FILE = DownloadFileFixtures.aDownloadFile().build();
    private static final Long DOWNLOAD_FILE_BYTES_DOWNLOADED = 1000L;
    private static final DownloadBatchTitle DOWNLOAD_BATCH_TITLE = DownloadBatchTitleFixtures.aDownloadBatchTitle().build();
    private static final DownloadBatchId DOWNLOAD_BATCH_ID = DownloadBatchIdFixtures.aDownloadBatchId().build();
    private static final InternalDownloadBatchStatus INTERNAL_DOWNLOAD_BATCH_STATUS = InternalDownloadBatchStatusFixtures.anInternalDownloadsBatchStatus().build();

    private final DownloadsBatchPersistence downloadsBatchPersistence = mock(DownloadsBatchPersistence.class);
    private final CallbackThrottle callbackThrottle = mock(CallbackThrottle.class);
    private final DownloadBatchCallback downloadBatchCallback = mock(DownloadBatchCallback.class);

    private DownloadBatch downloadBatch;

    @Before
    public void setUp() {
        Map<DownloadFileId, Long> bytesDownloaded = new HashMap<>();
        bytesDownloaded.put(DOWNLOAD_FILE.id(), DOWNLOAD_FILE_BYTES_DOWNLOADED);

        List<DownloadFile> downloadFiles = new ArrayList<>();
        downloadFiles.add(DOWNLOAD_FILE);

        DownloadBatchTitle downloadBatchTitleSpy = spy(DOWNLOAD_BATCH_TITLE);
        DownloadBatchId downloadBatchIdSpy = spy(DOWNLOAD_BATCH_ID);
        List<DownloadFile> downloadFilesSpy = spy(downloadFiles);
        Map<DownloadFileId, Long> bytesDownloadedSpy = spy(bytesDownloaded);
        InternalDownloadBatchStatus internalDownloadBatchStatusSpy = spy(INTERNAL_DOWNLOAD_BATCH_STATUS);

        downloadBatch = new DownloadBatch(
                downloadBatchTitleSpy,
                downloadBatchIdSpy,
                downloadFilesSpy,
                bytesDownloadedSpy,
                internalDownloadBatchStatusSpy,
                downloadsBatchPersistence,
                callbackThrottle
        );
    }

    @Test
    public void setsCallbackOnThrottle() {
        downloadBatch.setCallback(downloadBatchCallback);

        verify(callbackThrottle).setCallback(downloadBatchCallback);
    }

}
