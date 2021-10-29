import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.base64.Base64Encoder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * writeOutBound와 readOutBound를 이용한 인코더 테스트.
 */
public class Base64EncoderTest {
    public  boolean isMarketOpened() {
        Calendar openTime = Calendar.getInstance();
        Calendar closeTime = Calendar.getInstance();
        openTime.set(Calendar.HOUR_OF_DAY, 14);
        openTime.set(Calendar.MINUTE, 17);
        openTime.set(Calendar.SECOND, 0);
        openTime.set(Calendar.MILLISECOND, 0);
        closeTime.set(Calendar.HOUR_OF_DAY, 15);
        closeTime.set(Calendar.MINUTE, 30);
        closeTime.set(Calendar.SECOND, 0);
        closeTime.set(Calendar.MILLISECOND, 0);
        System.out.println("System.currentTimeMillis():"+System.currentTimeMillis());
        System.out.println(closeTime.getTimeInMillis());
        return   System.currentTimeMillis() < closeTime.getTimeInMillis() && System.currentTimeMillis() >= openTime.getTimeInMillis();
    }
    @Test
    public void time2() {



            //1.현재 시간을 가져와서
            Calendar current = Calendar.getInstance();
            //2. 오전9시 ~ 오후 3시30분이 아니면(장중이 아니면)

            if (isMarketOpened()) {
                //슬렉메세지 보냄
                System.out.println("장중입니다, 슬랙 보내기 ");
                System.out.println("현재시간은 : " + current.getTime() + "입니다");
            } else { //장중이 아닌경우
                //슬랙 메세지보내지 않음.
                System.out.println("장중이 아닙니다.");
                System.out.println("현재시간은 : " + current.getTime() + "입니다");
            }

        }

    @Test
    public void time() {
        //1.현재 시간을 가져와서
        LocalDateTime now = LocalDateTime.now();
        //2. 오전9시 ~ 오후 3시30분이 아니면
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.of(13,55,00));
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.of(15,30,00));
        System.out.println("now :" + now );
        System.out.println("start : " + start);
        System.out.println("end : " + end);
        if (now.isAfter(start) && now.isBefore(end)) { // 오전9시 ~ 오후 3시30분 전
            //슬렉메세지 보냄
            System.out.println("장중입니다, 슬랙 보내기 ");
            System.out.println("현재시간은 : " + now + "입니다");
        } else { //장중이 아닌경우
            //슬랙 메세지보내지 않음.
            System.out.println("장중이 아닙니다.");
            System.out.println("현재시간은 : " + now + "입니다");
        }
        System.out.println(now.getHour() +" "+ now.getMinute());
        //3.슬랙메세지보내지안는다.

        System.out.println(LocalDateTime.now());

//        LocalDateTime.of();
        DateTimeFormatter.ofPattern("HH:mm:ss");
    }


    @Test
    public void testEncoder() {
        String writeData = "안녕하세요";
        ByteBuf request = Unpooled.wrappedBuffer(writeData.getBytes(StandardCharsets.UTF_8));

        Base64Encoder encoder = new Base64Encoder(); //테스트를 위한 Base64Encoder 객체를 생성.
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(encoder); //EmbeddedChannel에 Base64Encoder객체를 등록

        embeddedChannel.writeOutbound(request); //writeOutbound메서드로 EmbededChannel의 아웃바운드에 데이터를 기록.
        ByteBuf response = (ByteBuf) embeddedChannel.readOutbound(); //readOutbound메서드로 Base64Encoder의 인코딩결과를 조회한다.

        String expect = "7JWI64WV7ZWY7IS47JqU"; //안녕하세요 의 base64인코딩 값
        assertEquals(expect, response.toString(Charset.defaultCharset()));

    }

    @Test
    public void sdf() {
        long a = 30L; //100억
        long b = 100000000000L; //1000억
        System.out.println(String.format("100억 입력한다. %10d", a));
        System.out.println(String.format("1000억 입력한다. %10d", b));
        Logger logger = LoggerFactory.getLogger(this.getClass());
        String msg = "나는야 천재다 그래서 그대는날잊고사는지이";
        StringBuilder sb = new StringBuilder("Closed stock code :");
        sb.append("goooood");
        logger.info("[sdf] : {}", sb);
        logger.warn("[sdf] : {}", sb);
        logger.error("[sdf] : {}", sb);
        logger.info("ss {}/{}", a, b);
    }

    @Test
    public void ffffasdfsadf() {
        StockMasterResponse res = new StockMasterResponse();

        StockMasterData data1 = new StockMasterData("1");
        StockMasterData data2 = new StockMasterData("2");
        StockMasterData data3 = new StockMasterData("3");
        StockMasterData data4 = new StockMasterData("4");
        StockMasterData data5 = new StockMasterData("5");

        res.setStocksList(Arrays.asList(data1,data2,data3,data4));

        for (StockMasterData data : res.getStocksList()) {
            System.out.println(data.getTickerSymbol());
        }
    }
    @Test
    public void fff13123fasdfsadf() throws ParseException {
//        System.out.println(SecurityGroup.isEtf(null));
//        System.out.println(SecurityGroup.isEtf(SecurityGroup.ETF));
//        System.out.println(SecurityGroup.isEtf(SecurityGroup.ETN));
        Date d = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(d));

        Calendar openTime = Calendar.getInstance();

        String openingTime = "09:00";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date = format.parse(openingTime);
        cal.setTime(date);
        System.out.println(date);

        openTime.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        openTime.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));

        System.out.println("성공 : OpenTime(" + openTime.get(Calendar.HOUR_OF_DAY) + ":" + openTime.get(Calendar.MINUTE) + ")");

        System.out.println(SecurityGroup.DR);
        System.out.println(SecurityGroup.FOREIGN_ETF.name());
    }

}
