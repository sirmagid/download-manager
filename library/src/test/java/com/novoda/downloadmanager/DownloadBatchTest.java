package com.novoda.downloadmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static com.google.common.truth.Truth.assertThat;
import static com.novoda.downloadmanager.DownloadBatchIdFixtures.aDownloadBatchId;
import static com.novoda.downloadmanager.DownloadBatchTitleFixtures.aDownloadBatchTitle;
import static com.novoda.downloadmanager.DownloadFileFixtures.aDownloadFile;
import static com.novoda.downloadmanager.DownloadFileStatusFixtures.aDownloadFileStatus;
import static com.novoda.downloadmanager.InternalDownloadBatchStatusFixtures.anInternalDownloadsBatchStatus;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.*;

public class DownloadBatchTest {

    private static final Long DOWNLOAD_FILE_BYTES_DOWNLOADED = 1000L;

    private final DownloadFile downloadFile = spy(aDownloadFile().build());
    private final DownloadBatchTitle downloadBatchTitle = spy(aDownloadBatchTitle().build());
    private final DownloadBatchId downloadBatchId = spy(aDownloadBatchId().build());
    private final InternalDownloadBatchStatus downloadBatchStatus = spy(anInternalDownloadsBatchStatus().build());
    private final DownloadsBatchPersistence downloadsBatchPersistence = mock(DownloadsBatchPersistence.class);
    private final CallbackThrottle callbackThrottle = mock(CallbackThrottle.class);
    private final DownloadBatchCallback downloadBatchCallback = mock(DownloadBatchCallback.class);
    private final Map<DownloadFileId, Long> bytesDownloaded = spy(new HashMap<DownloadFileId, Long>());
    private final List<DownloadFile> downloadFiles = spy(new ArrayList<DownloadFile>());

    private DownloadBatch downloadBatch;
    private DownloadFileStatus downloadFileStatus = aDownloadFileStatus().build();

    @Before
    public void setUp() {
        bytesDownloaded.put(downloadFile.id(), DOWNLOAD_FILE_BYTES_DOWNLOADED);
        downloadFiles.add(downloadFile);

        final ArgumentCaptor<DownloadFile.Callback> downloadFileCallbackCaptor = ArgumentCaptor.forClass(DownloadFile.Callback.class);
        willAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                downloadFileCallbackCaptor.getValue().onUpdate(downloadFileStatus);
                return null;
            }
        }).given(downloadFile).download(downloadFileCallbackCaptor.capture());

        downloadBatch = new DownloadBatch(
                downloadBatchTitle,
                downloadBatchId,
                downloadFiles,
                bytesDownloaded,
                downloadBatchStatus,
                downloadsBatchPersistence,
                callbackThrottle
        );
        downloadBatch.setCallback(downloadBatchCallback);
        resetMocks();
    }

    @Test
    public void setsCallbackOnThrottle() {
        downloadBatch.setCallback(downloadBatchCallback);

        verify(callbackThrottle).setCallback(downloadBatchCallback);
    }

    @Test
    public void doesNotEmit_whenCallbackIsAbsent() {
        downloadBatch.setCallback(null);

        downloadBatch.download();

        verifyZeroInteractions(downloadBatchCallback);
    }

    @Test
    public void doesNothing_whenStatusIsPaused() {
        given(downloadBatchStatus.status()).willReturn(DownloadBatchStatus.Status.PAUSED);

        downloadBatch.download();

        doesNothing();
    }

    @Test
    public void doesNothing_whenStatusIsDeleted() {
        given(downloadBatchStatus.status()).willReturn(DownloadBatchStatus.Status.DELETION);

        downloadBatch.download();

        doesNothing();
    }

    @Test
    public void markAsDownloading() {
        downloadBatch.download();

        verify(downloadBatchStatus).markAsDownloading(downloadsBatchPersistence);
    }

    @Test
    public void emitsStatus_whenDownloading() {
        downloadBatch.download();

        verify(downloadBatchCallback).onUpdate(downloadBatchStatus);
    }

    @Test
    public void marksAsError_whenBatchSizeIsZero() {
        given(downloadFile.getTotalSize()).willReturn(0L);

        downloadBatch.download();

        verify(downloadBatchStatus).markAsError(new DownloadError(DownloadError.Error.NETWORK_ERROR_CANNOT_DOWNLOAD_FILE), downloadsBatchPersistence);
    }

    @Test
    public void emitsErrorStatus_whenBatchSizeIsZero() {
        given(downloadFile.getTotalSize()).willReturn(0L);

        downloadBatch.download();

        assertThat(downloadBatchStatus.getDownloadErrorType()).isEqualTo(DownloadError.Error.NETWORK_ERROR_CANNOT_DOWNLOAD_FILE);
    }

    @Test
    public void doesNotScheduleFileDownload_whenMarkedAsError() {
        given(downloadFile.getTotalSize()).willReturn(0L);

        downloadBatch.download();

        verify(downloadFile, never()).download(any(DownloadFile.Callback.class));
    }

    @Test
    public void schedulesFileDownload() {
        downloadBatch.download();

        verify(downloadFile).download(any(DownloadFile.Callback.class));
    }

    @Test
    public void stopsDownloadingFiles_whenBatchCannotContinue() {
        DownloadFile additionalDownloadFile = mock(DownloadFile.class);
        downloadFiles.add(additionalDownloadFile);
        downloadFileStatus.markAsError(DownloadError.Error.UNKNOWN);

        downloadBatch.download();

        verify(additionalDownloadFile, never()).download(any(DownloadFile.Callback.class));
    }

    @Test
    public void marksAsDownloaded_whenStatusIsDownloaded() { // TODO: Marking something that is already marked? This persists to db which update didn't do.
        given(downloadBatchStatus.status()).willReturn(DownloadBatchStatus.Status.DOWNLOADED);

        downloadBatch.download();

        verify(downloadBatchStatus).markAsDownloaded(downloadsBatchPersistence);
    }

    @Test
    public void updateThroughThrottle_whenDownloading() {
        downloadBatch.download();

        verify(callbackThrottle).update(downloadBatchStatus);
    }

    @Test
    public void stopUpdatesThroughThrottle_whenDownloading() {
        downloadBatch.download();

        verify(callbackThrottle).stopUpdates();
    }

    private void resetMocks() {
        reset(
                downloadBatchTitle,
                downloadBatchId,
                downloadFiles,
                bytesDownloaded,
                downloadBatchStatus,
                downloadsBatchPersistence,
                callbackThrottle
        );
    }

    private void doesNothing() {
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
