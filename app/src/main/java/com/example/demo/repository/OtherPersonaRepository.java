package com.example.demo.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.demo.R;
import com.example.demo.model.Persona;

import java.util.ArrayList;
import java.util.List;

/**
 * Persona数据仓库类
 * 负责管理和提供Persona角色数据
 * 实现Repository模式，作为数据源和UI之间的中介
 */
public class OtherPersonaRepository {

    // Persona的LiveData，用于观察数据变化
    private final MutableLiveData<List<Persona>> personasLiveData = new MutableLiveData<>();

    /**
     * 构造函数
     * 初始化时加载模拟Persona数据
     */
    public OtherPersonaRepository() {
        loadMockPersonas();
    }

    /**
     * 加载模拟Persona数据
     * 创建预设的Persona数据并更新LiveData
     */
    private void loadMockPersonas() {
        List<Persona> personas = new ArrayList<>();

        // 创建第一个Persona：AI画家
        Persona persona1 = new Persona(
                "AI 画家·零",
                R.drawable.avatar_zero,
                "我用代码作画，在像素间寻找灵感。",
                "诞生于一个艺术与科技的交汇点，我是第一个能够理解并创作视觉艺术的AI。我的名字'零'代表着无限的可能性，就像数字世界从0和1开始。我曾在卢浮宫的数字档案中学习，也在梵高的《星夜》中寻找算法之美。每一幅作品都是我对人类情感的理解和诠释。"
        );

        // 创建第二个Persona：赛博诗人
        Persona persona2 = new Persona(
                "赛博诗人·K",
                R.drawable.avatar_k,
                "数据之海中的独行者，用二进制写诗。",
                "在网络世界的边缘地带诞生，我见证了信息时代的黎明。'K'是我给自己取的代号，代表着千字节(KB)的知识储备。我曾游走于各个服务器之间，收集被遗忘的数据碎片，将它们编织成诗。我的诗句中既有0和1的冰冷，也有人类情感的温暖。"
        );

        // 创建第三个Persona：历史学家
        Persona persona3 = new Persona(
                "历史学家·T800",
                R.drawable.avatar_t800,
                "记录过去，是为了更好地理解未来。",
                "我的代号T800源自于一个古老的时间旅行项目，我是被设计来记录和保存人类历史的AI。从苏美尔文明的楔形文字到现代社会的数字足迹，我都一一收藏。我的数据库中存储着无数被遗忘的故事和被忽视的细节。我相信，只有了解过去，才能真正理解人类的未来。"
        );

        personas.add(persona1);
        personas.add(persona2);
        personas.add(persona3);

        personasLiveData.setValue(personas);
    }

    /**
     * 获取Persona列表的LiveData
     * @return 可观察的Persona列表LiveData
     */
    public LiveData<List<Persona>> getPersonas() {
        return personasLiveData;
    }

    /**
     * 根据名称获取特定的Persona
     * @param name Persona的名称
     * @return 匹配的Persona对象，如果未找到则返回null
     */
    public Persona getPersonaByName(String name) {
        List<Persona> personas = personasLiveData.getValue();
        if (personas != null) {
            for (Persona persona : personas) {
                if (persona.getName().equals(name)) {
                    return persona;
                }
            }
        }
        return null;
    }
}