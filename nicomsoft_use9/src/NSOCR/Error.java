
package NSOCR;

public interface Error 
{
  public final static int ERROR_FIRST              = 0x70000000;
  public final static int ERROR_FILENOTFOUND       = 0x70000001;
  public final static int ERROR_LOADFILE           = 0x70000002;
  public final static int ERROR_SAVEFILE           = 0x70000003;
  public final static int ERROR_MISSEDIMGLOADER    = 0x70000004;
  public final static int ERROR_OPTIONNOTFOUND     = 0x70000005;
  public final static int ERROR_NOBLOCKS           = 0x70000006;
  public final static int ERROR_BLOCKNOTFOUND      = 0x70000007;
  public final static int ERROR_INVALIDINDEX       = 0x70000008;
  public final static int ERROR_INVALIDPARAMETER   = 0x70000009;
  public final static int ERROR_FAILED             = 0x7000000A;
  public final static int ERROR_INVALIDBLOCKTYPE   = 0x7000000B;
  public final static int ERROR_EMPTYTEXT          = 0x7000000D;
  public final static int ERROR_LOADINGDICTIONARY  = 0x7000000E;
  public final static int ERROR_LOADCHARBASE       = 0x7000000F;
  public final static int ERROR_NOMEMORY           = 0x70000010;
  public final static int ERROR_CANNOTLOADGS       = 0x70000011;
  public final static int ERROR_CANNOTPROCESSPDF   = 0x70000012;
  public final static int ERROR_NOIMAGE            = 0x70000013;
  public final static int ERROR_MISSEDSTEP         = 0x70000014;
  public final static int ERROR_OUTOFIMAGE         = 0x70000015;
  public final static int ERROR_EXCEPTION          = 0x70000016;
  public final static int ERROR_NOTALLOWED         = 0x70000017;
  public final static int ERROR_NODEFAULTDEVICE    = 0x70000018;
  public final static int ERROR_NOTAPPLICABLE      = 0x70000019;
  public final static int ERROR_MISSEDBARCODEDLL   = 0x7000001A;
  public final static int ERROR_PENDING            = 0x7000001B;
  public final static int ERROR_OPERATIONCANCELLED = 0x7000001C;
  public final static int ERROR_INVALIDOBJECT      = 0x70010000;
  public final static int ERROR_TOOMANYOBJECTS     = 0x70010001;
  public final static int ERROR_DLLNOTLOADED       = 0x70010002;
  public final static int ERROR_DEMO               = 0x70010003;
  public final static int ERROR_TOOMANYLANGUAGES   = 0x7000001D;  
}
