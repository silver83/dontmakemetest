/*
 *    Copyright (c) 2006 LiXiao.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.thoughtworks.shadow.ant;

import junit.textui.TestRunner;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Java;

import java.io.File;
import java.net.URL;

public class AntJavaTaskAdapter {
    private Project project;
    private Java java;

    public AntJavaTaskAdapter(String testClassName) {
        project = new Project();
        project.setName(testClassName);
        project.init();

        Target target = target(testClassName);
        project.addTarget(target);
        project.setDefault(target.getName());
    }

    public void execute() {
        new AntProjectExecutor().executeDefaultTarget(project);
    }

    public void addBuildListener(BuildListener buildListener) {
        project.addBuildListener(buildListener);
    }

    public void appendClasspaths(URL[] classpaths) {
        java.createClasspath().append(AntUtils.toPath(project, classpaths));
    }

    private Target target(String testClassName) {
        Target target = new Target();
        target.setName(testClassName);
        target.addTask(javaTask(testClassName));
        return target;
    }

    private Java javaTask(String testClassName) {
        java = (Java) project.createTask("java");
        java.setFork(true);
        java.setNewenvironment(true);
        java.setClassname(TestRunner.class.getName());
        java.setFailonerror(true);
        java.createArg().setValue(testClassName);
        return java;
    }

    public void setFileEncodeing(String encoding) {
        java.createJvmarg().setValue("-Dfile.encoding=" + encoding);
    }

    public void setBaseDir(File baseDir) {
        project.setBaseDir(baseDir);
    }

    public void setJvm(String jvm) {
        java.setJvm(jvm);
    }

    public void setJvmVersion(String jvmVersion) {
        java.setJVMVersion(jvmVersion);
    }

    public void setMaxMemory(String maxMemory) {
        java.setMaxmemory(maxMemory);
    }
}