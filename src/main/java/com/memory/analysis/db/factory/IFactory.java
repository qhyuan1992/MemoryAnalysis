package com.memory.analysis.db.factory;

import com.memory.analysis.db.ClassResultDao;
import com.memory.analysis.db.InstanceResultDao;

/**
 * @author cainjiang
 * @date 2018/6/1
 */
public interface IFactory {
    InstanceResultDao createInstanceResultDao();
    ClassResultDao createClassResultDao();
}
