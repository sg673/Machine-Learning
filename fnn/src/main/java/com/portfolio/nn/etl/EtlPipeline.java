package com.portfolio.nn.etl;

public interface EtlPipeline {
  class Dataset<S, T> {
        private final S data;
        private final T labels;
        
        public Dataset(S data, T labels) {
            this.data = data;
            this.labels = labels;
        }
        
        public S getImages() { return data; }
        public T getLabels() { return labels; }
    }

  public boolean Extract();
  public boolean Transform();
  public boolean Load();

  public boolean Run();
}
