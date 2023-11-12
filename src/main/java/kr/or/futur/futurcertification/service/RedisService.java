package kr.or.futur.futurcertification.service;

public interface RedisService {
    /**
     * Redis 연결 여부 확인
     * @return boolean
     */
    boolean isConnected();

    /**
     * 특정 데이터를 저장
     * @param key 키
     * @param value 값
     * @param duration 만료 시간
     */
    void setDataExpire(String key, String value, long duration);

    /**
     * 특정 키 값 삭제
     * @param key 키
     */
    void deleteData(String key);

    /**
     * 특정 키의 값을 가져옴
     * @param key 키
     * @return 값
     */
    String getData(String key);

    /**
     * 특정 키에 해당하는 값의 존재 유무
     * @param key 키
     * @return 값의 존재 유무
     */
    boolean existData(String key);
}
