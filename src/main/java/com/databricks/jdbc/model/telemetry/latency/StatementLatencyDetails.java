package com.databricks.jdbc.model.telemetry.latency;

public class StatementLatencyDetails {
  private ChunkDetails chunkDetails;

  public StatementLatencyDetails(ChunkDetails chunkDetails) {
    this.chunkDetails = chunkDetails;
  }

  public ChunkDetails getChunkDetails() {
    return chunkDetails;
  }

  public void setChunkDetails(ChunkDetails chunkDetails) {
    this.chunkDetails = chunkDetails;
  }
}
