package com.travel.service;

import com.travel.dto.TravelPlanRequest;
import com.travel.dto.TravelPlanResponse;
import com.travel.dto.DestinationResponse;
import com.travel.dto.AttractionResponse;
import com.travel.entity.*;
import com.travel.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiTravelPlanningService {
    
    private final TravelPlanRepository travelPlanRepository;
    private final DestinationRepository destinationRepository;
    private final AttractionRepository attractionRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public TravelPlanResponse generateTravelPlan(TravelPlanRequest request, Long userId) {
        log.info("开始生成AI旅游计划，用户ID: {}, 目的地: {}", userId, request.getDestination());
        
        // 创建旅游计划
        TravelPlan travelPlan = new TravelPlan();
        travelPlan.setTitle(request.getTitle());
        travelPlan.setDescription(request.getDescription());
        travelPlan.setStartDate(request.getStartDate());
        travelPlan.setEndDate(request.getEndDate());
        travelPlan.setBudget(request.getBudget());
        travelPlan.setStatus(TravelPlan.PlanStatus.PLANNING);
        travelPlan.setAiGenerated(true);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        travelPlan.setUser(user);
        
        travelPlan = travelPlanRepository.save(travelPlan);
        
        // 计算旅游天数
        long days = ChronoUnit.DAYS.between(request.getStartDate().toLocalDate(), 
                                          request.getEndDate().toLocalDate()) + 1;
        
        // 生成目的地和景点
        List<Destination> destinations = generateDestinations(travelPlan, request, (int) days);
        travelPlan.setDestinations(destinations);
        
        // 更新计划状态
        travelPlan.setStatus(TravelPlan.PlanStatus.CONFIRMED);
        travelPlan = travelPlanRepository.save(travelPlan);
        
        log.info("AI旅游计划生成完成，计划ID: {}", travelPlan.getId());
        
        return convertToResponse(travelPlan);
    }
    
    private List<Destination> generateDestinations(TravelPlan travelPlan, TravelPlanRequest request, int days) {
        List<Destination> destinations = new ArrayList<>();
        
        // 获取城市景点数据
        Map<String, List<AttractionData>> cityAttractions = getCityAttractions(request.getDestination());
        
        int dayOrder = 1;
        for (Map.Entry<String, List<AttractionData>> entry : cityAttractions.entrySet()) {
            String city = entry.getKey();
            List<AttractionData> attractions = entry.getValue();
            
            // 基于用户偏好智能筛选景点
            List<AttractionData> filteredAttractions = filterAttractionsByPreferences(attractions, request);
            
            // 根据旅游天数智能分配景点
            List<List<AttractionData>> dailyAttractions = distributeAttractionsByDays(filteredAttractions, days, request.getTravelStyle());
            
            for (List<AttractionData> dayAttractions : dailyAttractions) {
                if (dayOrder > days) break;
            
            // 创建目的地
            Destination destination = new Destination();
            destination.setName(city);
                destination.setDescription(generateDestinationDescription(city, request.getTravelStyle()));
            destination.setCity(city);
            destination.setCountry("中国");
            destination.setType(Destination.DestinationType.CITY);
                destination.setRating(calculateDestinationRating(dayAttractions));
            destination.setDayOrder(dayOrder);
            destination.setTravelPlan(travelPlan);
            
            // 设置坐标（这里使用模拟数据，实际应该从地图API获取）
                destination.setLatitude(getCityLatitude(city));
                destination.setLongitude(getCityLongitude(city));
            destination.setAddress(city + "市中心");
            
            destination = destinationRepository.save(destination);
            
            // 创建景点
            List<Attraction> destinationAttractions = new ArrayList<>();
            for (int i = 0; i < dayAttractions.size(); i++) {
                AttractionData attractionData = dayAttractions.get(i);
                
                Attraction attraction = new Attraction();
                attraction.setName(attractionData.name);
                attraction.setDescription(attractionData.description);
                attraction.setType(attractionData.type);
                attraction.setRating(attractionData.rating);
                attraction.setVisitDuration(attractionData.visitDuration);
                attraction.setTicketPrice(attractionData.ticketPrice);
                attraction.setOpeningHours(attractionData.openingHours);
                attraction.setDestination(destination);
                
                // 设置坐标
                attraction.setLatitude(destination.getLatitude() + (Math.random() - 0.5) * 0.01);
                attraction.setLongitude(destination.getLongitude() + (Math.random() - 0.5) * 0.01);
                attraction.setAddress(attractionData.name + "地址");
                
                attraction = attractionRepository.save(attraction);
                destinationAttractions.add(attraction);
            }
            
            destination.setAttractions(destinationAttractions);
            destinations.add(destination);
            
            dayOrder++;
            }
        }
        
        return destinations;
    }
    
    // 基于用户偏好筛选景点
    private List<AttractionData> filterAttractionsByPreferences(List<AttractionData> attractions, TravelPlanRequest request) {
        log.info("开始筛选景点，原始景点数量: {}", attractions.size());
        
        List<AttractionData> filtered = attractions.stream()
                .filter(attraction -> matchesTravelStyle(attraction, request.getTravelStyle()))
                .collect(Collectors.toList());
        log.info("旅游风格筛选后景点数量: {}", filtered.size());
        
        // 如果旅游风格筛选后没有景点，返回原始景点
        if (filtered.isEmpty()) {
            log.warn("旅游风格筛选后没有景点，返回原始景点");
            filtered = attractions;
        }
        
        // 按评分排序
        filtered = filtered.stream()
                .sorted((a, b) -> Double.compare(b.rating, a.rating))
                .collect(Collectors.toList());
        
        log.info("最终筛选后景点数量: {}", filtered.size());
        return filtered;
    }
    
    // 检查景点是否匹配旅游风格
    private boolean matchesTravelStyle(AttractionData attraction, String travelStyle) {
        if (travelStyle == null) return true;
        
        switch (travelStyle) {
            case "relax":
                return attraction.type == Attraction.AttractionType.PARK || 
                       attraction.type == Attraction.AttractionType.GARDEN ||
                       attraction.type == Attraction.AttractionType.BEACH;
            case "culture":
                return attraction.type == Attraction.AttractionType.MUSEUM ||
                       attraction.type == Attraction.AttractionType.HISTORIC ||
                       attraction.type == Attraction.AttractionType.RELIGIOUS ||
                       attraction.type == Attraction.AttractionType.CULTURAL;
            case "food":
                return attraction.type == Attraction.AttractionType.FOOD ||
                       attraction.type == Attraction.AttractionType.HISTORIC; // 美食街通常在历史街区
            case "nature":
                return attraction.type == Attraction.AttractionType.PARK ||
                       attraction.type == Attraction.AttractionType.MOUNTAIN ||
                       attraction.type == Attraction.AttractionType.BEACH;
            case "adventure":
                return attraction.type == Attraction.AttractionType.MOUNTAIN ||
                       attraction.type == Attraction.AttractionType.THEME_PARK;
            case "shopping":
                return attraction.type == Attraction.AttractionType.SHOPPING ||
                       attraction.type == Attraction.AttractionType.HISTORIC; // 商业街
            default:
                return true;
        }
    }
    
    // 检查景点是否匹配兴趣爱好
    private boolean matchesInterests(AttractionData attraction, List<String> interests) {
        if (interests == null || interests.isEmpty()) return true;
        
        for (String interest : interests) {
            switch (interest) {
                case "历史古迹":
                    if (attraction.type == Attraction.AttractionType.HISTORIC ||
                        attraction.type == Attraction.AttractionType.MONUMENT ||
                        attraction.type == Attraction.AttractionType.RELIGIOUS) return true;
                    break;
                case "自然风光":
                    if (attraction.type == Attraction.AttractionType.PARK ||
                        attraction.type == Attraction.AttractionType.MOUNTAIN ||
                        attraction.type == Attraction.AttractionType.BEACH) return true;
                    break;
                case "美食体验":
                    if (attraction.type == Attraction.AttractionType.FOOD) return true;
                    break;
                case "购物娱乐":
                    if (attraction.type == Attraction.AttractionType.SHOPPING ||
                        attraction.type == Attraction.AttractionType.ENTERTAINMENT) return true;
                    break;
                case "文化艺术":
                    if (attraction.type == Attraction.AttractionType.MUSEUM ||
                        attraction.type == Attraction.AttractionType.CULTURAL) return true;
                    break;
                case "户外运动":
                    if (attraction.type == Attraction.AttractionType.MOUNTAIN ||
                        attraction.type == Attraction.AttractionType.PARK) return true;
                    break;
            }
        }
        return false;
    }
    
    // 检查景点是否匹配预算
    private boolean matchesBudget(AttractionData attraction, Double budget, Integer days) {
        if (budget == null || days == null) return true;
        
        double dailyBudget = budget / days;
        double attractionCost = attraction.ticketPrice;
        
        // 如果景点门票超过每日预算的30%，则过滤掉
        return attractionCost <= dailyBudget * 0.3;
    }
    
    // 根据旅游天数智能分配景点
    private List<List<AttractionData>> distributeAttractionsByDays(List<AttractionData> attractions, int days, String travelStyle) {
        List<List<AttractionData>> dailyAttractions = new ArrayList<>();
        
        log.info("开始分配景点，总景点数: {}, 天数: {}", attractions.size(), days);
        
        // 根据旅游风格确定每天的景点数量
        int attractionsPerDay = getAttractionsPerDay(travelStyle);
        log.info("每天景点数量: {}", attractionsPerDay);
        
        // 如果景点数量不足，调整分配策略
        if (attractions.size() < days * attractionsPerDay) {
            attractionsPerDay = Math.max(1, attractions.size() / days);
            log.info("景点数量不足，调整每天景点数量为: {}", attractionsPerDay);
        }
        
        for (int day = 0; day < days; day++) {
            int startIndex = day * attractionsPerDay;
            int endIndex = Math.min(startIndex + attractionsPerDay, attractions.size());
            
            if (startIndex >= attractions.size()) {
                // 如果景点不够，循环使用现有景点
                startIndex = startIndex % attractions.size();
                endIndex = Math.min(startIndex + attractionsPerDay, attractions.size());
            }
            
            List<AttractionData> dayAttractions = attractions.subList(startIndex, endIndex);
            dailyAttractions.add(dayAttractions);
            log.info("第{}天分配景点数量: {}", day + 1, dayAttractions.size());
        }
        
        return dailyAttractions;
    }
    
    // 根据旅游风格确定每天的景点数量
    private int getAttractionsPerDay(String travelStyle) {
        switch (travelStyle) {
            case "relax":
                return 2; // 休闲度假，景点少一些
            case "culture":
                return 3; // 文化探索，适中
            case "food":
                return 4; // 美食之旅，可以多去几个地方
            case "nature":
                return 3; // 自然风光，适中
            case "adventure":
                return 2; // 冒险体验，体力消耗大，景点少一些
            case "shopping":
                return 4; // 购物娱乐，可以多逛几个地方
            default:
                return 3; // 默认
        }
    }
    
    // 生成目的地描述
    private String generateDestinationDescription(String city, String travelStyle) {
        String baseDescription = "AI智能推荐的" + city + "旅游目的地";
        
        switch (travelStyle) {
            case "relax":
                return baseDescription + "，适合休闲度假，享受慢生活";
            case "culture":
                return baseDescription + "，深度体验当地历史文化";
            case "food":
                return baseDescription + "，品尝地道美食，感受当地饮食文化";
            case "nature":
                return baseDescription + "，亲近自然，欣赏美丽风光";
            case "adventure":
                return baseDescription + "，挑战自我，体验刺激活动";
            case "shopping":
                return baseDescription + "，购物娱乐，享受都市生活";
            default:
                return baseDescription;
        }
    }
    
    // 计算目的地评分
    private Double calculateDestinationRating(List<AttractionData> attractions) {
        if (attractions.isEmpty()) return 4.0;
        
        double totalRating = attractions.stream()
                .mapToDouble(attraction -> attraction.rating)
                .sum();
        
        return totalRating / attractions.size();
    }
    
    // 获取城市纬度
    private Double getCityLatitude(String city) {
        // 这里应该从地图API获取真实坐标，现在使用模拟数据
        Map<String, Double> cityCoordinates = Map.of(
            "北京", 39.9042,
            "上海", 31.2304,
            "杭州", 30.2741,
            "苏州", 31.2989,
            "成都", 30.5728,
            "西安", 34.3416,
            "广州", 23.1291,
            "深圳", 22.5431
        );
        return cityCoordinates.getOrDefault(city, 39.9042);
    }
    
    // 获取城市经度
    private Double getCityLongitude(String city) {
        // 这里应该从地图API获取真实坐标，现在使用模拟数据
        Map<String, Double> cityCoordinates = Map.of(
            "北京", 116.4074,
            "上海", 121.4737,
            "杭州", 120.1551,
            "苏州", 120.5853,
            "成都", 104.0668,
            "西安", 108.9398,
            "广州", 113.2644,
            "深圳", 114.0579
        );
        return cityCoordinates.getOrDefault(city, 116.4074);
    }
    
    private Map<String, List<AttractionData>> getCityAttractions(String destination) {
        Map<String, List<AttractionData>> cityAttractions = new HashMap<>();
        
        // 扩展的城市和景点数据
        if (destination.contains("北京") || destination.contains("beijing")) {
            cityAttractions.put("北京", Arrays.asList(
                new AttractionData("故宫博物院", "明清两朝的皇家宫殿，世界文化遗产", Attraction.AttractionType.MUSEUM, 4.8, 180, 60.0, "08:30-17:00"),
                new AttractionData("天安门广场", "世界最大的城市广场", Attraction.AttractionType.MONUMENT, 4.6, 60, 0.0, "全天开放"),
                new AttractionData("颐和园", "中国古典园林之首，皇家园林", Attraction.AttractionType.PARK, 4.7, 120, 30.0, "06:30-18:00"),
                new AttractionData("八达岭长城", "万里长城最著名的一段", Attraction.AttractionType.MONUMENT, 4.9, 240, 40.0, "06:30-19:00"),
                new AttractionData("天坛公园", "明清皇帝祭天的场所", Attraction.AttractionType.PARK, 4.6, 90, 15.0, "06:00-22:00"),
                new AttractionData("什刹海", "北京内城唯一一处具有开阔水面的开放型景区", Attraction.AttractionType.PARK, 4.4, 120, 0.0, "全天开放"),
                new AttractionData("798艺术区", "当代艺术聚集地", Attraction.AttractionType.CULTURAL, 4.3, 150, 0.0, "10:00-18:00")
            ));
        } else if (destination.contains("上海") || destination.contains("shanghai")) {
            cityAttractions.put("上海", Arrays.asList(
                new AttractionData("外滩", "上海最著名的观光景点，万国建筑博览群", Attraction.AttractionType.MONUMENT, 4.7, 90, 0.0, "全天开放"),
                new AttractionData("东方明珠", "上海地标性建筑，468米高塔", Attraction.AttractionType.BUILDING, 4.5, 120, 160.0, "08:00-21:30"),
                new AttractionData("豫园", "明代私人花园，江南古典园林", Attraction.AttractionType.GARDEN, 4.4, 90, 30.0, "08:30-17:30"),
                new AttractionData("上海博物馆", "中国古代艺术博物馆", Attraction.AttractionType.MUSEUM, 4.6, 150, 0.0, "09:00-17:00"),
                new AttractionData("田子坊", "上海创意产业聚集地", Attraction.AttractionType.CULTURAL, 4.2, 120, 0.0, "10:00-22:00"),
                new AttractionData("新天地", "上海时尚地标", Attraction.AttractionType.SHOPPING, 4.3, 90, 0.0, "10:00-22:00"),
                new AttractionData("朱家角古镇", "上海保存最完整的江南水乡古镇", Attraction.AttractionType.HISTORIC, 4.4, 180, 0.0, "08:00-17:00")
            ));
        } else if (destination.contains("杭州") || destination.contains("hangzhou")) {
            cityAttractions.put("杭州", Arrays.asList(
                new AttractionData("西湖", "世界文化遗产，中国最著名的湖泊", Attraction.AttractionType.PARK, 4.8, 240, 0.0, "全天开放"),
                new AttractionData("雷峰塔", "白蛇传说的发源地", Attraction.AttractionType.MONUMENT, 4.5, 90, 40.0, "08:00-17:30"),
                new AttractionData("灵隐寺", "江南著名古刹", Attraction.AttractionType.RELIGIOUS, 4.6, 120, 45.0, "07:00-18:00"),
                new AttractionData("三潭印月", "西湖十景之一", Attraction.AttractionType.PARK, 4.7, 60, 20.0, "08:00-17:00"),
                new AttractionData("断桥残雪", "西湖十景之一", Attraction.AttractionType.MONUMENT, 4.6, 30, 0.0, "全天开放"),
                new AttractionData("河坊街", "杭州历史文化街区", Attraction.AttractionType.HISTORIC, 4.3, 90, 0.0, "全天开放")
            ));
        } else if (destination.contains("苏州") || destination.contains("suzhou")) {
            cityAttractions.put("苏州", Arrays.asList(
                new AttractionData("拙政园", "江南古典园林的代表作品", Attraction.AttractionType.GARDEN, 4.7, 120, 70.0, "07:30-17:30"),
                new AttractionData("虎丘", "吴中第一名胜", Attraction.AttractionType.PARK, 4.6, 90, 60.0, "07:30-17:30"),
                new AttractionData("留园", "中国四大名园之一", Attraction.AttractionType.GARDEN, 4.5, 90, 45.0, "07:30-17:30"),
                new AttractionData("周庄古镇", "中国第一水乡", Attraction.AttractionType.HISTORIC, 4.4, 180, 100.0, "08:00-21:00"),
                new AttractionData("平江路", "苏州古街，历史文化街区", Attraction.AttractionType.HISTORIC, 4.3, 90, 0.0, "全天开放"),
                new AttractionData("苏州博物馆", "贝聿铭设计的现代建筑", Attraction.AttractionType.MUSEUM, 4.5, 120, 0.0, "09:00-17:00")
            ));
        } else if (destination.contains("成都") || destination.contains("chengdu")) {
            cityAttractions.put("成都", Arrays.asList(
                new AttractionData("大熊猫繁育研究基地", "世界著名的大熊猫科研繁育基地", Attraction.AttractionType.ZOO, 4.8, 180, 58.0, "07:30-18:00"),
                new AttractionData("宽窄巷子", "成都历史文化街区", Attraction.AttractionType.HISTORIC, 4.4, 120, 0.0, "全天开放"),
                new AttractionData("锦里古街", "成都最古老的商业街", Attraction.AttractionType.HISTORIC, 4.3, 90, 0.0, "全天开放"),
                new AttractionData("都江堰", "世界文化遗产，古代水利工程", Attraction.AttractionType.MONUMENT, 4.7, 180, 80.0, "08:00-18:00"),
                new AttractionData("青城山", "道教发源地之一", Attraction.AttractionType.MOUNTAIN, 4.6, 240, 80.0, "08:00-18:00"),
                new AttractionData("杜甫草堂", "唐代诗人杜甫的故居", Attraction.AttractionType.HISTORIC, 4.5, 90, 50.0, "08:00-18:00")
            ));
        } else if (destination.contains("西安") || destination.contains("xian")) {
            cityAttractions.put("西安", Arrays.asList(
                new AttractionData("兵马俑", "世界文化遗产，秦始皇帝陵", Attraction.AttractionType.MONUMENT, 4.9, 180, 120.0, "08:30-17:30"),
                new AttractionData("大雁塔", "唐代佛教建筑", Attraction.AttractionType.RELIGIOUS, 4.6, 90, 50.0, "08:00-17:30"),
                new AttractionData("明城墙", "中国现存最完整的古代城垣", Attraction.AttractionType.MONUMENT, 4.5, 120, 54.0, "08:00-22:00"),
                new AttractionData("华清宫", "唐代皇家园林", Attraction.AttractionType.PARK, 4.4, 120, 110.0, "07:00-19:00"),
                new AttractionData("回民街", "西安著名的小吃街", Attraction.AttractionType.FOOD, 4.3, 90, 0.0, "全天开放"),
                new AttractionData("陕西历史博物馆", "中国第一座大型现代化博物馆", Attraction.AttractionType.MUSEUM, 4.7, 150, 0.0, "09:00-17:30")
            ));
        } else if (destination.contains("广州") || destination.contains("guangzhou")) {
            cityAttractions.put("广州", Arrays.asList(
                new AttractionData("广州塔", "世界第三高塔，小蛮腰", Attraction.AttractionType.BUILDING, 4.5, 120, 150.0, "09:00-23:00"),
                new AttractionData("陈家祠", "广东民间工艺博物馆", Attraction.AttractionType.MUSEUM, 4.6, 90, 10.0, "08:30-17:30"),
                new AttractionData("沙面岛", "广州最具欧陆风情的地方", Attraction.AttractionType.HISTORIC, 4.4, 120, 0.0, "全天开放"),
                new AttractionData("白云山", "广州的绿肺", Attraction.AttractionType.MOUNTAIN, 4.3, 180, 5.0, "06:00-21:00"),
                new AttractionData("北京路步行街", "广州最繁华的商业街", Attraction.AttractionType.SHOPPING, 4.2, 90, 0.0, "全天开放"),
                new AttractionData("珠江夜游", "欣赏广州夜景的最佳方式", Attraction.AttractionType.ENTERTAINMENT, 4.4, 90, 68.0, "19:00-21:00")
            ));
        } else if (destination.contains("深圳") || destination.contains("shenzhen")) {
            cityAttractions.put("深圳", Arrays.asList(
                new AttractionData("世界之窗", "世界著名景观的微缩景区", Attraction.AttractionType.THEME_PARK, 4.4, 240, 200.0, "09:00-22:00"),
                new AttractionData("欢乐谷", "大型主题乐园", Attraction.AttractionType.THEME_PARK, 4.5, 300, 230.0, "09:30-22:00"),
                new AttractionData("东部华侨城", "生态旅游度假区", Attraction.AttractionType.PARK, 4.3, 360, 180.0, "09:00-18:00"),
                new AttractionData("深圳湾公园", "城市滨海公园", Attraction.AttractionType.PARK, 4.2, 120, 0.0, "全天开放"),
                new AttractionData("莲花山公园", "深圳中心区的重要景点", Attraction.AttractionType.PARK, 4.1, 90, 0.0, "全天开放"),
                new AttractionData("大梅沙海滨公园", "深圳最著名的海滩", Attraction.AttractionType.BEACH, 4.0, 180, 0.0, "全天开放")
            ));
        } else {
            // 默认景点 - 根据目的地智能推荐
            cityAttractions.put(destination, generateDefaultAttractions(destination));
        }
        
        return cityAttractions;
    }
    
    private List<AttractionData> generateDefaultAttractions(String destination) {
        return Arrays.asList(
            new AttractionData(destination + "市中心广场", "城市中心广场，市民休闲的好去处", Attraction.AttractionType.PARK, 4.0, 60, 0.0, "全天开放"),
            new AttractionData(destination + "历史博物馆", "了解当地历史文化的重要场所", Attraction.AttractionType.MUSEUM, 4.2, 120, 20.0, "09:00-17:00"),
            new AttractionData(destination + "城市公园", "美丽的城市公园，适合休闲散步", Attraction.AttractionType.PARK, 4.1, 90, 0.0, "06:00-22:00"),
            new AttractionData(destination + "商业街", "当地著名的商业购物区", Attraction.AttractionType.SHOPPING, 4.0, 120, 0.0, "10:00-22:00"),
            new AttractionData(destination + "美食街", "品尝当地特色美食的好地方", Attraction.AttractionType.FOOD, 4.3, 90, 0.0, "全天开放")
        );
    }
    
    private TravelPlanResponse convertToResponse(TravelPlan travelPlan) {
        TravelPlanResponse response = new TravelPlanResponse();
        response.setId(travelPlan.getId());
        response.setTitle(travelPlan.getTitle());
        response.setDescription(travelPlan.getDescription());
        response.setStartDate(travelPlan.getStartDate());
        response.setEndDate(travelPlan.getEndDate());
        response.setBudget(travelPlan.getBudget());
        response.setStatus(travelPlan.getStatus());
        response.setAiGenerated(travelPlan.getAiGenerated());
        response.setUserId(travelPlan.getUser().getId());
        response.setCreatedAt(travelPlan.getCreatedAt());
        response.setUpdatedAt(travelPlan.getUpdatedAt());
        
        // 转换目的地
        List<DestinationResponse> destinationResponses = travelPlan.getDestinations().stream()
                .map(this::convertDestinationToResponse)
                .collect(Collectors.toList());
        response.setDestinations(destinationResponses);
        
        return response;
    }
    
    private DestinationResponse convertDestinationToResponse(Destination destination) {
        DestinationResponse response = new DestinationResponse();
        response.setId(destination.getId());
        response.setName(destination.getName());
        response.setDescription(destination.getDescription());
        response.setLatitude(destination.getLatitude());
        response.setLongitude(destination.getLongitude());
        response.setAddress(destination.getAddress());
        response.setCity(destination.getCity());
        response.setCountry(destination.getCountry());
        response.setType(destination.getType());
        response.setRating(destination.getRating());
        response.setVisitDuration(destination.getVisitDuration());
        response.setEstimatedCost(destination.getEstimatedCost());
        response.setDayOrder(destination.getDayOrder());
        response.setTimeOrder(destination.getTimeOrder());
        response.setCreatedAt(destination.getCreatedAt());
        response.setUpdatedAt(destination.getUpdatedAt());
        
        // 转换景点
        List<AttractionResponse> attractionResponses = destination.getAttractions().stream()
                .map(this::convertAttractionToResponse)
                .collect(Collectors.toList());
        response.setAttractions(attractionResponses);
        
        return response;
    }
    
    private AttractionResponse convertAttractionToResponse(Attraction attraction) {
        AttractionResponse response = new AttractionResponse();
        response.setId(attraction.getId());
        response.setName(attraction.getName());
        response.setDescription(attraction.getDescription());
        response.setLatitude(attraction.getLatitude());
        response.setLongitude(attraction.getLongitude());
        response.setAddress(attraction.getAddress());
        response.setType(attraction.getType());
        response.setRating(attraction.getRating());
        response.setVisitDuration(attraction.getVisitDuration());
        response.setTicketPrice(attraction.getTicketPrice());
        response.setOpeningHours(attraction.getOpeningHours());
        response.setPhone(attraction.getPhone());
        response.setWebsite(attraction.getWebsite());
        response.setImageUrl(attraction.getImageUrl());
        response.setDestinationId(attraction.getDestination().getId());
        response.setCreatedAt(attraction.getCreatedAt());
        response.setUpdatedAt(attraction.getUpdatedAt());
        
        return response;
    }
    
    // 获取用户的所有旅游计划
    public List<TravelPlanResponse> getUserTravelPlans(Long userId) {
        log.info("获取用户旅游计划，用户ID: {}", userId);
        
        List<TravelPlan> plans = travelPlanRepository.findByUserId(userId);
        return plans.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // 根据ID获取旅游计划
    public TravelPlanResponse getTravelPlanById(Long id) {
        log.info("获取旅游计划详情，ID: {}", id);
        
        TravelPlan plan = travelPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("旅游计划不存在"));
        
        return convertToResponse(plan);
    }
    
    // 更新旅游计划
    @Transactional
    public TravelPlanResponse updateTravelPlan(Long id, TravelPlanRequest request) {
        log.info("更新旅游计划，ID: {}", id);
        
        TravelPlan plan = travelPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("旅游计划不存在"));
        
        // 更新基本信息
        plan.setTitle(request.getTitle());
        plan.setDescription(request.getDescription());
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setBudget(request.getBudget());
        
        plan = travelPlanRepository.save(plan);
        
        return convertToResponse(plan);
    }
    
    // 删除旅游计划
    @Transactional
    public void deleteTravelPlan(Long id) {
        log.info("删除旅游计划，ID: {}", id);
        
        TravelPlan plan = travelPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("旅游计划不存在"));
        
        travelPlanRepository.delete(plan);
    }
    
    // 内部类用于存储景点数据
    private static class AttractionData {
        String name;
        String description;
        Attraction.AttractionType type;
        Double rating;
        Integer visitDuration;
        Double ticketPrice;
        String openingHours;
        
        AttractionData(String name, String description, Attraction.AttractionType type, 
                      Double rating, Integer visitDuration, Double ticketPrice, String openingHours) {
            this.name = name;
            this.description = description;
            this.type = type;
            this.rating = rating;
            this.visitDuration = visitDuration;
            this.ticketPrice = ticketPrice;
            this.openingHours = openingHours;
        }
    }
}
