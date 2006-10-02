package com.thoughtworks.fireworks.adapters;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFile;
import com.thoughtworks.fireworks.core.FireworksConfig;
import com.thoughtworks.fireworks.core.Utils;
import com.thoughtworks.shadow.ant.AntSunshine;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ModuleAdapter {
    private final Module module;
    private final FireworksConfig config;

    public ModuleAdapter(Module module, FireworksConfig config) {
        this.module = module;
        this.config = config;
    }

    AntSunshine antSunshine() {
        return new AntSunshine(classpaths(), encoding(), baseDir(), jvm(), jvmVersion(), config.maxMemory());
    }

    private String jvmVersion() {
        return Utils.getJavaVersion(getJdk().getVersionString());
    }

    private String jvm() {
        return getJdk().getVMExecutablePath();
    }

    private URL[] classpaths() {
        VirtualFile[] files = manager().getFiles(OrderRootType.CLASSES_AND_OUTPUT);
        Set fileURLs = new HashSet();
        for (int i = 0; i < files.length; i++) {
            fileURLs.add(Utils.toURL(files[i].getPresentableUrl()));
        }
        VirtualFile test = manager().getCompilerOutputPathForTests();
        if (test != null) {
            fileURLs.add(Utils.toURL(test.getPresentableUrl()));
        }
        return (URL[]) fileURLs.toArray(new URL[fileURLs.size()]);
    }

    private File baseDir() {
        return new File(getModuleDir());
    }

    private String encoding() {
        return CharsetToolkit.getIDEOptionsCharset().name();
    }

    private String getModuleDir() {
        VirtualFile parent = module.getModuleFile().getParent();
        if (parent == null) {
            return ".";
        }
        return parent.getPresentableUrl();
    }

    private ProjectJdk getJdk() {
        ProjectJdk jdk = manager().getJdk();
        if (jdk == null) {
            jdk = ProjectRootManager.getInstance(module.getProject()).getProjectJdk();
        }
        if (jdk == null) {
            throw new IllegalArgumentException("the jdk of project and module does not set");
        }
        return jdk;
    }

    private ModuleRootManager manager() {
        return ModuleRootManager.getInstance(module);
    }

}
