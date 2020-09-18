package be.ipl.pae.exception;

@SuppressWarnings("serial")
public class BizException extends RuntimeException {

  public BizException(String errorMsg) {
    super(errorMsg);
  }
}
