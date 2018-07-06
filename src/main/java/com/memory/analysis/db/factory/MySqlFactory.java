package com.memory.analysis.db.factory;

import com.memory.analysis.db.*;

/**
 * @author cainjiang
 * @date 2018/6/1
 */
public class MySqlFactory implements IFactory {
    @Override
    public InstanceResultDao createInstanceResultDao() {
        return new InstanceResultMySqlDao();
    }

    @Override
    public ClassResultDao createClassResultDao() {
        return new ClassResultMySqlDao();
    }

    @Override
    public HandleResultDao createHandleResultDao() {
        return new HandleResultMySqlDao();
    }
}
