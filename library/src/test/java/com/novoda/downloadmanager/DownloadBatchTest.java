package com.novoda.downloadmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static com.novoda.downloadmanager.DownloadBatchIdFixtures.aDownloadBatchId;
import static com.novoda.downloadmanager.DownloadBatchTitleFixtures.aDownloadBatchTitle;
import static com.novoda.downloadmanager.DownloadFileFixtures.aDownloadFile;
import static com.novoda.downloadmanager.InternalDownloadBatchStatusFixtures.anInternalDownloadsBatchStatus;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class DownloadBatchTest {

    private static final DownloadFile DOWNLOAD_FILE = aDownloadFile().build();
    private static final Long DOWNLOAD_FILE_BYTES_DOWNLOADED = 1000L;

    private final DownloadBatchTitle downloadBatchTitle = spy(aDownloadBatchTitle().build());
    private final DownloadBatchId downloadBatchId = spy(aDownloadBatchId().build());
    private final InternalDownloadBatchStatus internalDownloadBatchStatus = spy(anInternalDownloadsBatchStatus().build());
    private final DownloadsBatchPersistence downloadsBatchPersistence = mock(DownloadsBatchPersistence.class);
    private final CallbackThrottle callbackThrottle = mock(CallbackThrottle.class);
    private final DownloadBatchCallback downloadBatchCallback = mock(DownloadBatchCallback.class);
    private final Map<DownloadFileId, Long> bytesDownloaded = spy(new HashMap<DownloadFileId, Long>());
    private final List<DownloadFile> downloadFiles = spy(new ArrayList<DownloadFile>());

    private DownloadBatch downloadBatch;

    @Before
    public void setUp() {
        bytesDownloaded.put(DOWNLOAD_FILE.id(), DOWNLOAD_FILE_BYTES_DOWNLOADED);
        downloadFiles.add(DOWNLOAD_FILE);
        reset(
                downloadBatchTitle,
                downloadBatchId,
                downloadFiles,
                bytesDownloaded,
                internalDownloadBatchStatus,
                downloadsBatchPersistence,
                callbackThrottle
        );

        downloadBatch = new DownloadBatch(
                downloadBatchTitle,
                downloadBatchId,
                downloadFiles,
                bytesDownloaded,
                internalDownloadBatchStatus,
                downloadsBatchPersistence,
                callbackThrottle
        );
    }

    @Test
    public void setsCallbackOnThrottle() {
        downloadBatch.setCallback(downloadBatchCallback);

        verify(callbackThrottle).setCallback(downloadBatchCallback);
    }

    @Test
    public void doesNothing_whenStatusIsPaused() {
        given(internalDownloadBatchStatus.status()).willReturn(DownloadBatchStatus.Status.PAUSED);

        downloadBatch.download();

        verifyZeroInteractions(
                downloadBatchTitle,
                downloadBatchId,
                downloadsBatchPersistence,
                callbackThrottle,
                downloadBatchCallback,
                bytesDownloaded,
                downloadFiles
        );
    }

}
