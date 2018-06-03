package com.memory.analysis.db.factory;

import com.memory.analysis.db.ClassResultMySqlDao;
import com.memory.analysis.db.ClassResultDao;
import com.memory.analysis.db.InstanceResultDao;
import com.memory.analysis.db.InstanceResultMySqlDao;

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
}
