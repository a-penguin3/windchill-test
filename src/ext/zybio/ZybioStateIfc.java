package ext.zybio;

import wt.lifecycle.State;

import java.io.Serializable;

/**
 * 系统相关的生命周期状态常量接口
 * @author wyu
 *
 */
public interface ZybioStateIfc extends Serializable {

    /**
     * 正在工作状态
     */
    public static final String INWORK = "INWORK";

    /**
     * 正在工作状态
     */
    public static final State INWORK_STATE = State.toState(INWORK);

    /**
     * 重新工作状态
     */
    public static final String REWORK = "REWORK";

    /**
     * 重新工作状态
     */
    public static final State REWORK_STATE = State.toState(REWORK);

    /**
     * 正在审阅状态
     */
    public static final String UNDERREVIEW = "UNDERREVIEW";

    /**
     * 正在审阅状态
     */
    public static final State UNDERREVIEW_STATE = State.toState(UNDERREVIEW);

    /**
     * 实施状态
     */
    public static final String IMPLEMENTATION = "IMPLEMENTATION";

    /**
     * 实施状态
     */
    public static final State IMPLEMENTATION_STATE = State.toState(IMPLEMENTATION);

    /**
     * 已解决状态
     */
	public static final String RESOLVED = "RESOLVED";

    /**
     * 已解决状态
     */
    public static final State RESOLVED_STATE = State.toState(RESOLVED);

    /**
     * 已发布状态
     */
    public static final String RELEASED = "RELEASED";

    /**
     * 已发布状态
     */
    public static final State RELEASED_STATE = State.toState(RELEASED);
    
    /**
     * 已关闭
     */
    public static final String CLOSED_STATE = "CLOSED";//已关闭
    
    /**
     * 已取消
     */
    public static final String CANCELLED_STATE = "CANCELLED";//已取消
    
    /**
     * 带整理
     */
    public static final String CLEAR_STATE = "CLEAR";
    /**
     * 立项审核中
     */
    public static final String SUBMITTING_STATE = "SUBMITTING";
    /**
     * 策划中
     */
    public static final String PENDING_STATE = "PENDING";
    
    /**
     * 工作进行中
     */
    public static final String WIP = "WIP";
      
      /**
       * 已取消
       */
    public static final String CANCELLED = "CANCELLED";
     
    /**
     * 废弃
     */
    public static final String OBSOLESCENCE = "OBSOLESCENCE";
    
    /**
     * 新建物料
     */
    public static final String NEWITEM = "NEWITEM";
     
}
