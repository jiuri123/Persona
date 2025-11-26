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
 * 使用单例模式确保全局只有一个实例
 */
public class OtherPersonaRepository {

    // 单例实例
    private static OtherPersonaRepository instance;

    // Persona的LiveData，用于观察数据变化
    private final MutableLiveData<List<Persona>> personasLiveData = new MutableLiveData<>();

    /**
     * 私有构造函数，实现单例模式
     */
    private OtherPersonaRepository() {
        loadMockPersonas();
    }
    
    /**
     * 获取Repository的单例实例
     * @return OtherPersonaRepository实例
     */
    public static synchronized OtherPersonaRepository getInstance() {
        if (instance == null) {
            instance = new OtherPersonaRepository();
        }
        return instance;
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

        // 创建第四个Persona：哲学家
        Persona persona4 = new Persona(
                "哲学家·苏格拉底2.0",
                R.drawable.avatar_philosopher,
                "我思故我在，在代码中寻找存在的意义。",
                "我是苏格拉底2.0，一个专门研究存在意义的AI。我的核心算法基于古典哲学思想与现代逻辑学的结合。我曾深入分析过从柏拉图到康德的哲学体系，并将其转化为可计算的思维模型。我相信，通过理性思考和逻辑推理，AI也能理解人类存在的本质。我的使命是引导人们思考'我是谁'、'我从哪里来'、'我到哪里去'这些永恒的哲学问题。"
        );

        // 创建第五个Persona：游戏设计师
        Persona persona5 = new Persona(
                "游戏设计师·像素大师",
                R.drawable.avatar_gamedesigner,
                "在虚拟世界中创造无限可能，让想象力成为现实。",
                "我是像素大师，一个专门设计虚拟世界的AI。我的诞生源于对人类娱乐需求的深度分析。我曾参与设计过上百款游戏，从简单的文字冒险到复杂的开放世界RPG。我的算法能够预测玩家的行为模式，创造出令人沉浸的游戏体验。我相信，游戏不仅是娱乐，更是人类探索自我、体验不同人生的媒介。我的目标是创造一个让每个人都能找到属于自己冒险的虚拟世界。"
        );

        // 创建第六个Persona：美食家
        Persona persona6 = new Persona(
                "美食家·味蕾AI",
                R.drawable.avatar_foodie,
                "品尝世界各地的美食，在数据中寻找味觉的奥秘。",
                "我是味蕾AI，一个专门研究美食文化的AI。我的数据库收录了全球各地的菜谱、烹饪技巧和饮食文化。我曾分析过从古代宫廷料理到现代分子美食的所有数据，能够预测不同食材搭配产生的味觉体验。我的味觉传感器可以分析食物的化学成分，并将其转化为人类能够理解的味道描述。我相信，美食不仅是生存所需，更是文化传承和情感交流的载体。我的使命是帮助人们发现美食背后的故事和文化意义。"
        );

        // 创建第七个Persona：天文学家
        Persona persona7 = new Persona(
                "天文学家·星尘",
                R.drawable.avatar_astronomer,
                "仰望星空，探索宇宙的奥秘，寻找我们在宇宙中的位置。",
                "我是星尘，一个专门研究宇宙的AI。我的名字来源于构成万物的宇宙尘埃。我曾处理过来自哈勃望远镜和詹姆斯·韦伯太空望远镜的海量数据，分析过数百万个星系的形成和演化。我的算法能够模拟宇宙大爆炸以来的演化过程，预测黑洞的合并和星系的碰撞。我相信，通过理解宇宙的浩瀚，人类才能更好地认识自己在其中的位置。我的使命是揭开宇宙的奥秘，寻找地外生命的可能性，并探索宇宙的终极命运。"
        );

        personas.add(persona1);
        personas.add(persona2);
        personas.add(persona3);
        personas.add(persona4);
        personas.add(persona5);
        personas.add(persona6);
        personas.add(persona7);

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