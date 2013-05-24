/*
 * Copyright (C) 2013 Yrom <http://www.yrom.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.yrom.builder;

import java.io.File;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class AntBuilder {

    private Project       project;
    
    public AntBuilder(String _buildFile, String _baseDir) throws Exception {
        init(_buildFile, _baseDir);
    }

    public void init(String _buildFile, String _baseDir) throws Exception {
        if (_buildFile == null)
            throw new IllegalArgumentException("project build file cannot be null");
        project = new Project();

        project.init();

        DefaultLogger consoleLogger = new DefaultLogger();
        consoleLogger.setErrorPrintStream(System.err);
        consoleLogger.setOutputPrintStream(System.out);
        consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
        project.addBuildListener(consoleLogger);

        // Set the base directory. If none is given, "." is used.
        if (_baseDir == null)
            _baseDir = new String(".");

        project.setBasedir(_baseDir);

        ProjectHelper.configureProject(project, new File(_buildFile));
    }

    public void runTarget(String _target) throws Exception {
        // Test if the project exists
        if (project == null)
            throw new Exception(
                    "No target can be launched because the project has not been initialized. Please call the 'init' method first !");
        // If no target is specified, run the default one.
        if (_target == null)
            _target = project.getDefaultTarget();

        // Run the target
        project.executeTarget(_target);

    }

}
