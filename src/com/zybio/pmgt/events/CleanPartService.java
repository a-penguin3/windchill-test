package com.zybio.pmgt.events;

import com.zybio.pmgt.listener.CleanPartListener;
import wt.doc.WTDocument;
import wt.events.KeyedEvent;
import wt.fc.PersistenceManagerEvent;
import wt.services.ManagerException;
import wt.services.ServiceEventListenerAdapter;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressServiceEvent;

import java.io.Serializable;

public class CleanPartService extends StandardManager implements CleanPartListener, Serializable {

    private ServiceEventListenerAdapter listener;

    private static final String CLASSNAME = CleanPartService.class.getName();

    @Override
    public String getConceptualClassname() {
        return CLASSNAME;
    }

    /**
     * 构建静态方法
     *
     * @return 监听器实例
     * @throws WTException WT异常
     */
    public static CleanPartService newCleanPartService() throws WTException {
        CleanPartService service = new CleanPartService();
        service.initialize();
        return service;
    }

    protected synchronized void performStartupProcess() throws ManagerException {
        //本身的getConceptualClassname方法是返回strandService的classname
        //我们需要重写getConceptualClassname方法，返回当前监听器的classname
        listener = new CleanPartAttrListenerImpl(getConceptualClassname());
        System.out.println("------------------进入限定文件主属性不能为空监听器-----------------");
        getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(PersistenceManagerEvent.INSERT));
        getManagerService().addEventListener(listener, WorkInProgressServiceEvent.generateEventKey(WorkInProgressServiceEvent.POST_CHECKIN));
        System.out.println("----------------------------监听结束--------------------------");
    }

    /**
     * 处理监听具体功能的内部类
     */
    class CleanPartAttrListenerImpl extends ServiceEventListenerAdapter {

        public CleanPartAttrListenerImpl(String s) {
            super(s);
        }

        public void notifyVetoableEvent(Object obj) throws Exception {
            System.out.println("obj className is:" + obj.getClass().getName());

            //获得当前触发的事件对象
            KeyedEvent keyedEvent = (KeyedEvent) obj;
            //获取当前被操作的持久化对象，如部件，文档，容器等
            Object target = keyedEvent.getEventTarget();
            //获取事件类型
            String eventType = keyedEvent.getEventType();
            System.out.println("target=" + target + ",,,,eventType=" + eventType);
            if (eventType != null && (eventType.equals(PersistenceManagerEvent.INSERT) || eventType.equals(WorkInProgressServiceEvent.POST_CHECKIN))) {
                System.out.println(" notifyVetoableEvent b " + (target instanceof WTDocument));
                if (target instanceof WTDocument) {
                    WTDocument doc = (WTDocument) target;
                    System.out.println("文件对象为：" + doc);
                    String name = doc.getName();
                    System.out.println("文件名称为：" + name);
                    if (!name.equals("abc")){
                        throw new WTException("文件名称必须为abc");
                    }
                }
            }
        }
    }


}
