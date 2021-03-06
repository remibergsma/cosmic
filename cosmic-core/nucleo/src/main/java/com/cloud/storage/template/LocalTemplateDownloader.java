package com.cloud.storage.template;

import com.cloud.storage.StorageLayer;
import com.cloud.utils.exception.CloudRuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalTemplateDownloader extends TemplateDownloaderBase implements TemplateDownloader {
    public static final Logger s_logger = LoggerFactory.getLogger(LocalTemplateDownloader.class);

    public LocalTemplateDownloader(final StorageLayer storageLayer, final String downloadUrl, final String toDir,
                                   final long maxTemplateSizeInBytes, final DownloadCompleteCallback callback) {
        super(storageLayer, downloadUrl, toDir, maxTemplateSizeInBytes, callback);
        final String filename = new File(downloadUrl).getName();
        _toFile = toDir.endsWith(File.separator) ? toDir + filename : toDir + File.separator + filename;
    }

    public LocalTemplateDownloader(final StorageLayer storageLayer, final String downloadUrl, final String toDir,
                                   final long maxTemplateSizeInBytes) {
        super(storageLayer, downloadUrl, toDir, maxTemplateSizeInBytes, null);
        final String filename = new File(downloadUrl).getName();
        _toFile = toDir.endsWith(File.separator) ? toDir + filename : toDir + File.separator + filename;
    }

    public LocalTemplateDownloader(final String downloadUrl, final String toDir, final long maxTemplateSizeInBytes) {
        super(null, downloadUrl, toDir, maxTemplateSizeInBytes, null);
        final String filename = new File(downloadUrl).getName();
        _toFile = toDir.endsWith(File.separator) ? toDir + filename : toDir + File.separator + filename;
    }

    @Override
    public long download(final boolean resume, final DownloadCompleteCallback callback) {
        if (_status == Status.ABORTED || _status == Status.UNRECOVERABLE_ERROR || _status == Status.DOWNLOAD_FINISHED) {
            throw new CloudRuntimeException("Invalid status for downloading: " + _status);
        }

        _start = System.currentTimeMillis();
        _resume = resume;

        final File src;
        try {
            src = new File(new URI(_downloadUrl));
        } catch (final URISyntaxException e) {
            final String message = "Invalid URI " + _downloadUrl;
            s_logger.warn(message);
            _status = Status.UNRECOVERABLE_ERROR;
            throw new CloudRuntimeException(message, e);
        }

        final File dst = new File(_toFile);

        FileChannel fic = null;
        FileChannel foc = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            if (_storage != null) {
                dst.createNewFile();
                _storage.setWorldReadableAndWriteable(dst);
            }

            final ByteBuffer buffer = ByteBuffer.allocate(1024 * 512);

            try {
                fis = new FileInputStream(src);
            } catch (final FileNotFoundException e) {
                _errorString = "Unable to find " + _downloadUrl;
                s_logger.warn(_errorString);
                throw new CloudRuntimeException(_errorString, e);
            }
            fic = fis.getChannel();
            try {
                if (!dst.exists()) {
                    dst.delete();
                }
                fos = new FileOutputStream(dst);
            } catch (final FileNotFoundException e) {
                final String message = "Unable to find " + _toFile;
                s_logger.warn(message);
                throw new CloudRuntimeException(message, e);
            }
            foc = fos.getChannel();

            _remoteSize = src.length();
            _totalBytes = 0;
            _status = TemplateDownloader.Status.IN_PROGRESS;

            try {
                while (_status != Status.ABORTED && fic.read(buffer) != -1) {
                    buffer.flip();
                    final int count = foc.write(buffer);
                    _totalBytes += count;
                    buffer.clear();
                }
            } catch (final IOException e) {
                s_logger.warn("Unable to download");
            }

            String downloaded = "(incomplete download)";
            if (_totalBytes == _remoteSize) {
                _status = TemplateDownloader.Status.DOWNLOAD_FINISHED;
                downloaded = "(download complete)";
            }

            _errorString = "Downloaded " + _remoteSize + " bytes " + downloaded;
            _downloadTime += System.currentTimeMillis() - _start;
            return _totalBytes;
        } catch (final Exception e) {
            _status = TemplateDownloader.Status.UNRECOVERABLE_ERROR;
            _errorString = e.getMessage();
            throw new CloudRuntimeException(_errorString, e);
        } finally {
            if (fic != null) {
                try {
                    fic.close();
                } catch (final IOException e) {
                    s_logger.info("[ignore] error while closing file input channel.");
                }
            }

            if (foc != null) {
                try {
                    foc.close();
                } catch (final IOException e) {
                    s_logger.info("[ignore] error while closing file output channel.");
                }
            }

            if (fis != null) {
                try {
                    fis.close();
                } catch (final IOException e) {
                    s_logger.info("[ignore] error while closing file input stream.");
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (final IOException e) {
                    s_logger.info("[ignore] error while closing file output stream.");
                }
            }

            if (_status == Status.UNRECOVERABLE_ERROR && dst.exists()) {
                dst.delete();
            }
            if (callback != null) {
                callback.downloadComplete(_status);
            }
        }
    }
}
