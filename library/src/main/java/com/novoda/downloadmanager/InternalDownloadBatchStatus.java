package com.novoda.downloadmanager;

interface InternalDownloadBatchStatus extends DownloadBatchStatus {

    void update(long currentBytesDownloaded, long totalBatchSizeBytes);

    void markAsDownloading(DownloadsBatchStatusPersistence persistence);

    void markAsPaused(DownloadsBatchStatusPersistence persistence);

    void markAsQueued(DownloadsBatchStatusPersistence persistence);

    void markForDeletion();

    void markAsError(DownloadError downloadError, DownloadsBatchStatusPersistence persistence);

    void markAsDownloaded(DownloadsBatchStatusPersistence persistence);
}