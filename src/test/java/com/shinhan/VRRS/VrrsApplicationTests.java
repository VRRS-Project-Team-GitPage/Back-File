package com.shinhan.VRRS;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.shinhan.VRRS.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class VrrsApplicationTests {
	@Test
	void contextLoads() {
		extractImageText();
	}

	public void extractImageText() {
		List<String> processedText = new ArrayList<String>();
		try {
			String ocrResponse = "{\"version\":\"V2\",\"requestId\":\"ea1d8876-54d8-47b9-bf18-1c2add3d7248\",\"timestamp\":1726109194149,\"images\":[{\"uid\":\"13a9d07d82714105992661b704187045\",\"name\":\"demo\",\"inferResult\":\"SUCCESS\",\"message\":\"SUCCESS\",\"validationResult\":{\"result\":\"NO_REQUESTED\"},\"convertedImageInfo\":{\"width\":773,\"height\":878,\"pageIndex\":0,\"longImage\":false},\"fields\":[{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":38.0,\"y\":12.0},{\"x\":175.0,\"y\":15.0},{\"x\":174.0,\"y\":68.0},{\"x\":37.0,\"y\":65.0}]},\"inferText\":\"원재료명\",\"inferConfidence\":1.0,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":230.0,\"y\":8.0},{\"x\":710.0,\"y\":-1.0},{\"x\":711.0,\"y\":63.0},{\"x\":231.0,\"y\":71.0}]},\"inferText\":\"면/쌀가루(국내산),감자전분(국내산),\",\"inferConfidence\":0.9927,\"type\":\"NORMAL\",\"lineBreak\":true},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":231.0,\"y\":59.0},{\"x\":507.0,\"y\":52.0},{\"x\":508.0,\"y\":113.0},{\"x\":232.0,\"y\":120.0}]},\"inferText\":\"팜유(말레이시아산),\",\"inferConfidence\":0.9962,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":504.0,\"y\":60.0},{\"x\":624.0,\"y\":56.0},{\"x\":625.0,\"y\":108.0},{\"x\":505.0,\"y\":112.0}]},\"inferText\":\"변성전분,\",\"inferConfidence\":0.9997,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":621.0,\"y\":54.0},{\"x\":713.0,\"y\":54.0},{\"x\":713.0,\"y\":105.0},{\"x\":621.0,\"y\":105.0}]},\"inferText\":\"글루텐,\",\"inferConfidence\":0.9992,\"type\":\"NORMAL\",\"lineBreak\":true},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":234.0,\"y\":111.0},{\"x\":396.0,\"y\":111.0},{\"x\":396.0,\"y\":163.0},{\"x\":234.0,\"y\":163.0}]},\"inferText\":\"태움.용융소금,\",\"inferConfidence\":0.9916,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":390.0,\"y\":107.0},{\"x\":510.0,\"y\":107.0},{\"x\":510.0,\"y\":159.0},{\"x\":390.0,\"y\":159.0}]},\"inferText\":\"감미유-R,\",\"inferConfidence\":0.9983,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":502.0,\"y\":109.0},{\"x\":638.0,\"y\":102.0},{\"x\":641.0,\"y\":153.0},{\"x\":504.0,\"y\":160.0}]},\"inferText\":\"산도조절제,\",\"inferConfidence\":1.0,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":633.0,\"y\":102.0},{\"x\":719.0,\"y\":102.0},{\"x\":719.0,\"y\":153.0},{\"x\":633.0,\"y\":153.0}]},\"inferText\":\"구아검,\",\"inferConfidence\":0.9983,\"type\":\"NORMAL\",\"lineBreak\":true},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":233.0,\"y\":159.0},{\"x\":719.0,\"y\":147.0},{\"x\":720.0,\"y\":202.0},{\"x\":235.0,\"y\":213.0}]},\"inferText\":\"면류첨가알칼리제(탄산칼륨,탄산나트륨,피로\",\"inferConfidence\":0.9928,\"type\":\"NORMAL\",\"lineBreak\":true},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":234.0,\"y\":209.0},{\"x\":382.0,\"y\":206.0},{\"x\":383.0,\"y\":258.0},{\"x\":235.0,\"y\":261.0}]},\"inferText\":\"인산나트륨),\",\"inferConfidence\":0.994,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":381.0,\"y\":205.0},{\"x\":502.0,\"y\":205.0},{\"x\":502.0,\"y\":258.0},{\"x\":381.0,\"y\":258.0}]},\"inferText\":\"비타민B2\",\"inferConfidence\":0.9999,\"type\":\"NORMAL\",\"lineBreak\":true},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":230.0,\"y\":245.0},{\"x\":481.0,\"y\":245.0},{\"x\":481.0,\"y\":307.0},{\"x\":230.0,\"y\":307.0}]},\"inferText\":\"짜장소스/양파(국내산),\",\"inferConfidence\":0.9999,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":475.0,\"y\":247.0},{\"x\":558.0,\"y\":247.0},{\"x\":558.0,\"y\":303.0},{\"x\":475.0,\"y\":303.0}]},\"inferText\":\"정제수,\",\"inferConfidence\":0.9998,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":549.0,\"y\":241.0},{\"x\":734.0,\"y\":236.0},{\"x\":736.0,\"y\":297.0},{\"x\":551.0,\"y\":302.0}]},\"inferText\":\"춘장[밀(국내산),\",\"inferConfidence\":0.9979,\"type\":\"NORMAL\",\"lineBreak\":true},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":233.0,\"y\":297.0},{\"x\":392.0,\"y\":297.0},{\"x\":392.0,\"y\":356.0},{\"x\":233.0,\"y\":356.0}]},\"inferText\":\"대두(국내산),\",\"inferConfidence\":0.9962,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":383.0,\"y\":301.0},{\"x\":560.0,\"y\":298.0},{\"x\":561.0,\"y\":352.0},{\"x\":384.0,\"y\":354.0}]},\"inferText\":\"식염, 주정, 종국],\",\"inferConfidence\":0.9558,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":551.0,\"y\":290.0},{\"x\":737.0,\"y\":286.0},{\"x\":739.0,\"y\":349.0},{\"x\":552.0,\"y\":353.0}]},\"inferText\":\"양배추(국내산),\",\"inferConfidence\":0.9988,\"type\":\"NORMAL\",\"lineBreak\":true},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":231.0,\"y\":346.0},{\"x\":411.0,\"y\":343.0},{\"x\":412.0,\"y\":406.0},{\"x\":232.0,\"y\":409.0}]},\"inferText\":\"감자(국내산),\",\"inferConfidence\":0.9943,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":406.0,\"y\":350.0},{\"x\":652.0,\"y\":348.0},{\"x\":653.0,\"y\":400.0},{\"x\":406.0,\"y\":403.0}]},\"inferText\":\"우마미베이스비-115,\",\"inferConfidence\":0.9958,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":648.0,\"y\":345.0},{\"x\":739.0,\"y\":345.0},{\"x\":739.0,\"y\":398.0},{\"x\":648.0,\"y\":398.0}]},\"inferText\":\"백설탕,\",\"inferConfidence\":0.9999,\"type\":\"NORMAL\",\"lineBreak\":true},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":233.0,\"y\":401.0},{\"x\":330.0,\"y\":401.0},{\"x\":330.0,\"y\":458.0},{\"x\":233.0,\"y\":458.0}]},\"inferText\":\"현미유,\",\"inferConfidence\":0.9943,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":324.0,\"y\":400.0},{\"x\":445.0,\"y\":400.0},{\"x\":445.0,\"y\":456.0},{\"x\":324.0,\"y\":456.0}]},\"inferText\":\"돼지고기,\",\"inferConfidence\":0.9996,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":442.0,\"y\":398.0},{\"x\":599.0,\"y\":398.0},{\"x\":599.0,\"y\":452.0},{\"x\":442.0,\"y\":452.0}]},\"inferText\":\"조미돈지-P,\",\"inferConfidence\":0.994,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":595.0,\"y\":395.0},{\"x\":743.0,\"y\":395.0},{\"x\":743.0,\"y\":452.0},{\"x\":595.0,\"y\":452.0}]},\"inferText\":\"두류가공품,\",\"inferConfidence\":0.9995,\"type\":\"NORMAL\",\"lineBreak\":true},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":231.0,\"y\":453.0},{\"x\":345.0,\"y\":453.0},{\"x\":345.0,\"y\":508.0},{\"x\":231.0,\"y\":508.0}]},\"inferText\":\"변성전분,\",\"inferConfidence\":0.9997,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":338.0,\"y\":452.0},{\"x\":452.0,\"y\":452.0},{\"x\":452.0,\"y\":507.0},{\"x\":338.0,\"y\":507.0}]},\"inferText\":\"정제소금,\",\"inferConfidence\":0.9997,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":444.0,\"y\":449.0},{\"x\":582.0,\"y\":449.0},{\"x\":582.0,\"y\":505.0},{\"x\":444.0,\"y\":505.0}]},\"inferText\":\"당류가공품,\",\"inferConfidence\":0.9991,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":575.0,\"y\":449.0},{\"x\":737.0,\"y\":446.0},{\"x\":738.0,\"y\":502.0},{\"x\":577.0,\"y\":505.0}]},\"inferText\":\"조미오일(채종\",\"inferConfidence\":0.9993,\"type\":\"NORMAL\",\"lineBreak\":true},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":228.0,\"y\":506.0},{\"x\":431.0,\"y\":501.0},{\"x\":433.0,\"y\":559.0},{\"x\":229.0,\"y\":564.0}]},\"inferText\":\"유,건양따,컨마늘),\",\"inferConfidence\":0.9717,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":426.0,\"y\":495.0},{\"x\":607.0,\"y\":493.0},{\"x\":608.0,\"y\":559.0},{\"x\":427.0,\"y\":561.0}]},\"inferText\":\"마늘(국내산),\",\"inferConfidence\":0.9932,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":601.0,\"y\":499.0},{\"x\":740.0,\"y\":499.0},{\"x\":740.0,\"y\":557.0},{\"x\":601.0,\"y\":557.0}]},\"inferText\":\"파기름(대두\",\"inferConfidence\":0.9996,\"type\":\"NORMAL\",\"lineBreak\":true},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":231.0,\"y\":561.0},{\"x\":277.0,\"y\":561.0},{\"x\":277.0,\"y\":614.0},{\"x\":231.0,\"y\":614.0}]},\"inferText\":\"유,\",\"inferConfidence\":0.9975,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":272.0,\"y\":557.0},{\"x\":347.0,\"y\":557.0},{\"x\":347.0,\"y\":615.0},{\"x\":272.0,\"y\":615.0}]},\"inferText\":\"대파,\",\"inferConfidence\":1.0,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":342.0,\"y\":555.0},{\"x\":422.0,\"y\":554.0},{\"x\":423.0,\"y\":612.0},{\"x\":343.0,\"y\":614.0}]},\"inferText\":\"생강),\",\"inferConfidence\":0.9873,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":420.0,\"y\":554.0},{\"x\":749.0,\"y\":552.0},{\"x\":750.0,\"y\":608.0},{\"x\":420.0,\"y\":611.0}]},\"inferText\":\"로스티드오니온후레바오일,\",\"inferConfidence\":0.9892,\"type\":\"NORMAL\",\"lineBreak\":true},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":228.0,\"y\":604.0},{\"x\":420.0,\"y\":600.0},{\"x\":421.0,\"y\":670.0},{\"x\":229.0,\"y\":673.0}]},\"inferText\":\"생강(국내산),\",\"inferConfidence\":0.9949,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":416.0,\"y\":605.0},{\"x\":516.0,\"y\":605.0},{\"x\":516.0,\"y\":666.0},{\"x\":416.0,\"y\":666.0}]},\"inferText\":\"흑후추,\",\"inferConfidence\":0.9998,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":507.0,\"y\":599.0},{\"x\":756.0,\"y\":596.0},{\"x\":757.0,\"y\":666.0},{\"x\":508.0,\"y\":668.0}]},\"inferText\":\"수수분말(국내산)\",\"inferConfidence\":0.9999,\"type\":\"NORMAL\",\"lineBreak\":true},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":230.0,\"y\":680.0},{\"x\":272.0,\"y\":680.0},{\"x\":272.0,\"y\":733.0},{\"x\":230.0,\"y\":733.0}]},\"inferText\":\"밀,\",\"inferConfidence\":0.9943,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":265.0,\"y\":677.0},{\"x\":337.0,\"y\":677.0},{\"x\":337.0,\"y\":737.0},{\"x\":265.0,\"y\":737.0}]},\"inferText\":\"대두,\",\"inferConfidence\":0.9998,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":327.0,\"y\":674.0},{\"x\":447.0,\"y\":671.0},{\"x\":448.0,\"y\":732.0},{\"x\":328.0,\"y\":734.0}]},\"inferText\":\"돼지고기,\",\"inferConfidence\":0.9988,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":439.0,\"y\":670.0},{\"x\":620.0,\"y\":669.0},{\"x\":620.0,\"y\":731.0},{\"x\":439.0,\"y\":732.0}]},\"inferText\":\"조개류(바지락),\",\"inferConfidence\":0.9987,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":613.0,\"y\":669.0},{\"x\":702.0,\"y\":669.0},{\"x\":702.0,\"y\":731.0},{\"x\":613.0,\"y\":731.0}]},\"inferText\":\"쇠고기\",\"inferConfidence\":1.0,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":693.0,\"y\":667.0},{\"x\":756.0,\"y\":667.0},{\"x\":756.0,\"y\":727.0},{\"x\":693.0,\"y\":727.0}]},\"inferText\":\"함유\",\"inferConfidence\":1.0,\"type\":\"NORMAL\",\"lineBreak\":true},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":15.0,\"y\":749.0},{\"x\":111.0,\"y\":749.0},{\"x\":111.0,\"y\":807.0},{\"x\":15.0,\"y\":807.0}]},\"inferText\":\"성분명\",\"inferConfidence\":0.9985,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":111.0,\"y\":754.0},{\"x\":141.0,\"y\":754.0},{\"x\":141.0,\"y\":804.0},{\"x\":111.0,\"y\":804.0}]},\"inferText\":\"및\",\"inferConfidence\":1.0,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":138.0,\"y\":747.0},{\"x\":205.0,\"y\":747.0},{\"x\":205.0,\"y\":810.0},{\"x\":138.0,\"y\":810.0}]},\"inferText\":\"함량\",\"inferConfidence\":1.0,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":218.0,\"y\":746.0},{\"x\":280.0,\"y\":746.0},{\"x\":280.0,\"y\":816.0},{\"x\":218.0,\"y\":816.0}]},\"inferText\":\"면중\",\"inferConfidence\":0.9913,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":268.0,\"y\":744.0},{\"x\":520.0,\"y\":744.0},{\"x\":520.0,\"y\":816.0},{\"x\":268.0,\"y\":816.0}]},\"inferText\":\"쌀가루(국내산)30.11%,\",\"inferConfidence\":0.9969,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":508.0,\"y\":741.0},{\"x\":769.0,\"y\":740.0},{\"x\":769.0,\"y\":809.0},{\"x\":509.0,\"y\":811.0}]},\"inferText\":\"감자전분(국내산)19.99%\",\"inferConfidence\":0.9994,\"type\":\"NORMAL\",\"lineBreak\":true},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":218.0,\"y\":808.0},{\"x\":328.0,\"y\":808.0},{\"x\":328.0,\"y\":876.0},{\"x\":218.0,\"y\":876.0}]},\"inferText\":\"짜장소스\",\"inferConfidence\":1.0,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":323.0,\"y\":814.0},{\"x\":356.0,\"y\":814.0},{\"x\":356.0,\"y\":869.0},{\"x\":323.0,\"y\":869.0}]},\"inferText\":\"중\",\"inferConfidence\":1.0,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":350.0,\"y\":805.0},{\"x\":418.0,\"y\":805.0},{\"x\":418.0,\"y\":873.0},{\"x\":350.0,\"y\":873.0}]},\"inferText\":\"춘장\",\"inferConfidence\":0.9999,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":406.0,\"y\":803.0},{\"x\":623.0,\"y\":803.0},{\"x\":623.0,\"y\":872.0},{\"x\":406.0,\"y\":872.0}]},\"inferText\":\"10.1%[밀(국내산),\",\"inferConfidence\":0.9877,\"type\":\"NORMAL\",\"lineBreak\":false},{\"valueType\":\"ALL\",\"boundingPoly\":{\"vertices\":[{\"x\":611.0,\"y\":802.0},{\"x\":772.0,\"y\":792.0},{\"x\":776.0,\"y\":865.0},{\"x\":616.0,\"y\":875.0}]},\"inferText\":\"대두(국내산)]\",\"inferConfidence\":1.0,\"type\":\"NORMAL\",\"lineBreak\":true}]}]}";

			processedText = parseJson(ocrResponse); // 응답 텍스트 전처리
			for (String as : processedText) System.out.println(as);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> parseJson(String response) {
		//json 파싱
		Gson gson = new Gson();
		JsonObject jObj = gson.fromJson(response, JsonObject.class); // 테스트용

		//images 배열 -> obj 화
		JsonArray jArray = (JsonArray) jObj.get("images");
		JsonObject JSONObjImage = (JsonObject) jArray.get(0);
		JsonArray s = (JsonArray) JSONObjImage.get("fields");

		List<Map<String, Object>> m = JsonUtil.getListMapFromJsonArray(s);
		List<String> text = new ArrayList<>();
		List<double[]> vertices1 = new ArrayList<>(); // vertices 리스트 추가
		List<double[]> vertices2 = new ArrayList<>(); // vertices 리스트 추가

		for (Map<String, Object> as : m) {
			text.add((String) as.get("inferText"));

			// "boundingPoly"에서 "vertices"의 첫 번째 값을 추출
			Map<String, Object> boundingPoly = (Map<String, Object>) as.get("boundingPoly");
			List<Map<String, Object>> vertices = (List<Map<String, Object>>) boundingPoly.get("vertices");

			if (vertices != null && vertices.size() > 0) {
				Map<String, Object> vertex1 = vertices.get(0);
				double x = (double) vertex1.get("x");
				double y = (double) vertex1.get("y");

				// 첫 번째 vertex의 {x, y} 값을 배열로 저장
				vertices1.add(new double[]{x, y});

				Map<String, Object> vertex2 = vertices.get(1);
				x = (double) vertex2.get("x");
				y = (double) vertex2.get("y");

				// 두 번째 vertex의 {x, y} 값을 배열로 저장
				vertices2.add(new double[]{x, y});
			}
		}

		List<String> result = getIngredients1(text, vertices1, vertices2);
        return extractIngredients(result);
//		return result;
	}

	public List<String> getIngredients1(List<String> text, List<double[]> vertices1, List<double[]> vertices2) {
		double d = 2.0;

		List<String> ingredients = new ArrayList<>();
		boolean isStart = false;
		boolean isSub = false;
		boolean isNotEnd = false;
		String prev = "";
		double prevX = 0.0;
		double endX = 0.0;

		int index = 0;

		for (int i = 0; i < text.size(); i++) {
//			if (endX < vertices2.get(i)[0]) endX = vertices2.get(i)[0]; // 마지막 X 좌표로 업데이트
			if (isStart) { // 시작
				String txt = text.get(i);
				if (txt.contains("성분명") | txt.contains("함량")) break;
				if (ingredients.isEmpty()) {
					if (txt.contains("/")) {
						txt = txt.split("/")[1];
						ingredients.add(txt.replaceAll(" ", ""));
					} else ingredients.add(txt);
				} else {
					if (txt.contains("/")) {
						txt = txt.split("/")[1];
						ingredients.add(txt.replaceAll(" ", ""));
						index++;
					} else {
						String newText = ingredients.get(index) + txt.replaceAll(" ", "");
						ingredients.set(index, newText);
					}
				}
			}
			if (text.get(i).contains("원재료")) isStart = true;
		}

		return ingredients;
	}

	public static List<String> extractIngredients(List<String> input) {
		List<String> ingredients = new ArrayList<>();

		for (String in : input) {
			// 괄호 안의 원산지 또는 불필요한 정보는 제거, 괄호 안의 원재료는 각각 추출
			// 대괄호와 괄호를 처리하는 정규 표현식
			Pattern pattern = Pattern.compile("([^,\\[\\]()]+)(\\[([^]]*)\\])?(\\(([^)]*)\\))?");
			Matcher matcher = pattern.matcher(in);

			while (matcher.find()) {
				String ingredient = matcher.group(1).trim();  // 괄호 및 대괄호 밖의 원재료 추출
				String insideSquareBrackets = matcher.group(3);  // 대괄호 안의 원재료 추출
				String insideBrackets = matcher.group(5);        // 괄호 안의 원재료 추출

				boolean isIngredient = false; // 대괄호 원재료 추가 여부

				// 대괄호 안에 있는 것이 원산지 정보가 아닌 경우 처리
				if (insideSquareBrackets != null) {
					String[] subIngredients = insideSquareBrackets.split(","); // 원재료 분리
					for (String subIngredient : subIngredients) {
						String front = subIngredient.split("\\(")[0]; // 원재료 분리
						if (!front.matches(".*(국내산|말레이시아산|함량).*")) {
							isIngredient = true;
							ingredients.add(front.trim());
						}
					}
				}

				// 괄호 안에 있는 것이 원산지 정보가 아닌 경우 처리
				if (insideBrackets != null && !insideBrackets.matches(".*(국내산|말레이시아산).*")) {
					// 괄호 안의 원재료를 각각 분리
					String[] subIngredients = insideBrackets.split(",");
					for (String subIngredient : subIngredients) {
						isIngredient = true;
						ingredients.add(subIngredient.trim());
					}
				}

				if (!isIngredient) ingredients.add(ingredient);
			}
		}
		return ingredients;
	}
}