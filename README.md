# NotifyQQ
This is a Jenkins notify plugin, send a message to QQ when a job is finished.

I think this is a demand for Chinese only, so I will write the follow readme content in Chinese. if you want it change to English, please let me know.

## 使用说明

参考我的博客文章：[QQ机器人和Jenkins插件编写](http://omegayy.duapp.com/qqji-qi-ren-he-jenkinscha-jian-bian-xie/)
了解插件的工作流程。

简单来讲需要做如下准备：

1. QQ协议工具，Mojo-QQ，运行openqq模块
2. (可选)jenkins插件编写基础
3. 安装NotifyQQ插件，在job配置界面设置需要通知的QQ号，支持群号和个人号
4. 注意：该插件依赖Mojo-QQ的运行，否则不能正常工作。
