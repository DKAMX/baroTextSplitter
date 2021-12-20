# baroTextSplitter 潜渊症文本分离器
A small program used for split text from Item element  
用于分离Item元素中物品名称，描述等文本的小程序  
## How to use 如何使用  
1. Install JDK (version 11 or above, OpenJDK is also ok)  
   安装JDK（第11版本或者以上，OpenJDK也可以）  
2. check that you have the `textsplit.java` file, this is the actual code that the program run  
   Each time the program run, it will compile the source code again, to make sure it can run on this machine  
   同时检查同目录下有`textsplit.java`和文件，这是程序实际运行的代码  
   每次程序运行时，会重新编译一次源代码，确保程序能够在此电脑上正常运行  
3. Run the `RUN_textsplit.bat` file  
   运行`RUN_textsplit.bat`文件  
## Notice 注意事项  
Currently this program can only process XML files that relate to `Item` element  
Temporaily not catch the Exception so when you type an incorrect filename, the program will close  

目前程序只能处理和`Item`有关的XML文件  
暂时没有进行异常处理，所以当你输入了一个错误的文件名，程序会自动结束