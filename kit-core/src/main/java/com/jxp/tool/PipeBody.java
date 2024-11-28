package com.jxp.tool;

import java.io.IOException;

import javax.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Pipe;

public class PipeBody extends RequestBody {
    private final Pipe pipe;
    private final BufferedSink sink;
    private final MediaType mediaType;
    private final long defaultMaxBufferSize = 8192;

    public PipeBody(MediaType mediaType) {
        this.pipe = new Pipe(defaultMaxBufferSize);
        this.sink = Okio.buffer(pipe.sink());
        this.mediaType = mediaType;
    }

    public PipeBody(MediaType mediaType, long maxBufferSize) {
        this.pipe = new Pipe(maxBufferSize);
        this.sink = Okio.buffer(pipe.sink());
        this.mediaType = mediaType;
    }

    public BufferedSink sink() {
        return this.sink;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return this.mediaType;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        sink.writeAll(pipe.source());
    }
}
