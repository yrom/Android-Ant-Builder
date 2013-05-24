Android ant builder
=====
使用Ant 实现批量打包Android应用

基于这篇文章的一个实现 <http://blog.csdn.net/t12x3456/article/details/7957117>

如何使用
=====

###配置Ant环境
从Ant官网下载所需ant的jar包

这里以windows配置为例：

Windows下ANT用到的环境变量主要有2个，`ANT_HOME` 、`PATH`。

设置ANT_HOME指向ant的安装目录。

设置方法：
`ANT_HOME = D:/apache_ant_1.9.1`

将`%ANT_HOME%/bin; %ANT_HOME%/lib`添加到环境变量的path中。

设置方法：
`PATH = %PATH%; %ANT_HOME%/bin; %ANT_HOME%/lib`

###配置ant builder
1. 修改config.properties的各项value
具体含义见`Main.java`

2. market.txt里保存需要打包的市场标识
"#"表示注释

###配置需要打包的工程
初始化工作：（一般只需要操作初始化一次就够了）

1. 运行`android update project -p <你的工程目录>`可以自动生成ant 打包所需的 build.xml (包括依赖lib工程也需要执行一次)

2. 修改ant.properties中签名文件的路径和密码(如果没有请自行添加)
    key.store=D:\\android\\mykeystore
    key.store.password=123456
    key.alias=mykey
    key.alias.password=123456

*具体应用可以参见<https://github.com/yrom/acfunm>

###执行！
执行 Main.java即可
也可以编译成jar包，方便使用，这里就不做详细介绍了。

你也可以做相应的修改及完善

###欢迎Fork，Pull request :D

Copyright and License
======

	Copyright 2013 Yrom <http://www.yrom.net>

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

