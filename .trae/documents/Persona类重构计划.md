1. **修改Persona.java**：移除@Entity注解，使其成为抽象基类，保留所有公共属性和方法
2. **创建OtherPersona.java**：作为Persona子类，用于系统预设角色
3. **创建UserPersona.java**：作为Persona子类，添加@Entity注解用于本地存储
4. **修改数据访问层**：
   - 将PersonaDao.java重命名为UserPersonaDao.java，修改为操作UserPersona实体
   - 更新LocalDataSource.java，使用UserPersonaDao和UserPersona
5. **更新仓库层**：
   - 修改UserPersonaRepository，使用UserPersona
   - 修改OtherPersonaRepository，使用OtherPersona
   - 修改OtherPersonaPostRepository、OtherPersonaChatRepository等使用OtherPersona
6. **更新UI层**：
   - 修改UserPersonaChatActivity、UserPersonaCreateActivity等使用UserPersona
   - 修改OtherPersonaChatActivity等使用OtherPersona
   - 更新所有相关Adapter和ViewModel使用正确的Persona类型

重构后，系统预设角色将使用OtherPersona类，用户创建的角色将使用UserPersona类并实现本地化存储，结构更加清晰合理。