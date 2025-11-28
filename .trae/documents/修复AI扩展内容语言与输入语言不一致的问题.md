1. **问题分析**：在`UserPersonaPostRepository.java`中，`aiExpandContent`方法使用随机方式选择生成内容的语言，导致扩展内容语言与输入语言不一致。

2. **解决方案**：实现语言检测功能，根据用户输入内容自动判断语言类型，确保`aiExpandContent`生成的扩展内容语言与输入语言一致。

3. **实现步骤**：

   * 在`UserPersonaPostRepository.java`中添加`detectLanguage`方法，用于检测输入内容的主要语言

   * 修改`aiExpandContent`方法，使用检测到的语言生成扩展内容

4. **语言检测算法**：

   * 统计输入内容中中文字符的数量

   * 如果中文字符占比超过50%，则使用中文

   * 否则使用英文

   * 对于空输入或纯符号输入，默认使用中文

5. **文件修改**：

   * `e:\Android\Demo\app\src\main\java\com\example\demo\repository\UserPersonaPostRepository.java`

6. **预期效果**：

   * 用户输入中文时，AI扩展的内容为中文

   * 用户输入英文时，AI扩展的内容为英文

   * 中英文混合输入时，根据主要语言自动选择合适的扩展语言

