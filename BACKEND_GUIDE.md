# دليل تشغيل الـ Backend وتكامل واجهات API لتطبيق TK_SABR

يقدم تطبيق **TK_SABR** منظومة أمن سيبراني متقدمة لفحص وتحليل حسابات TikTok بدون تسجيل دخول أو إدخال كلمات مرور.

---

## 1. بنية جلب البيانات (Data Retrieval Architecture)

يعتمد التطبيق على معمارية مزدوجة ومحمية:

### أ) المسار المباشر (On-Device Direct Fetch):
- يقوم تطبيق الأندرويد بالاتصال المباشر بصفحات البروفايل العامة ومخدمات oEmbed الرسمية التابعة لـ TikTok عبر مكتبة `OkHttp` المهيأة برؤوس متصفح حقيقية (Desktop/Mobile User-Agent).
- استخراج البيانات الدقيقة من كائنات JSON المضمنة: `__UNIVERSAL_DATA_FOR_REHYDRATION__` و `SIGI_STATE`.
- استخراج المؤشرات العامة: المعرف (UID)، الاسم المعروض، المتابعين، المتابَعين، الإعجابات، الفيديوهات، حالة التوثيق بالعلامة الزرقاء، وحالة خصوصية الحساب.
- لا يتم استدعاء أي بيانات وهمية أو تجريبية؛ في حال تعذر الحصول على معلومة معينة يوضح التطبيق قيود الوصول بشفافية.

### ب) مسار الخادم الوسيط (Backend Proxy Architecture - اختياري للضغط العالي):
للمؤسسات والبيئات التي تتطلب آلاف عمليات الفحص اليومية وتدوير عناوين IP (Proxy Rotation)، يمكن ربط التطبيق بخادم وسيط وسيط (Node.js أو Python) أو مزود بيانات خارجي (مثل RapidAPI TikTok Data API).

---

## 2. خادم وسيط جاهز للتشغيل السريع (FastAPI / Node.js)

في حال رغبت بنشر سيرفر مخصص لحماية مفاتيح الـ API وإدارة الـ Caching ومعدل الطلبات (Rate Limiting):

### خيار 1: خادم Python (FastAPI):
```python
# main.py
from fastapi import FastAPI, HTTPException
import httpx
import re
import json

app = FastAPI(title="TK_SABR TikTok Audit Backend")

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36",
    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
    "Accept-Language": "en-US,en;q=0.9,ar;q=0.8"
}

@app.get("/api/audit/{username}")
async def audit_user(username: str):
    clean_username = username.strip().lstrip("@")
    url = f"https://www.tiktok.com/@{clean_username}"
    
    async with httpx.AsyncClient(follow_redirects=True, timeout=15.0) as client:
        res = await client.get(url, headers=HEADERS)
        if res.status_code == 404:
            raise HTTPException(status_code=404, detail="الحساب غير موجود أو تم حذفه")
        
        # البحث عن كتلة البيانات الكلية
        match = re.search(r'<script id="__UNIVERSAL_DATA_FOR_REHYDRATION__" type="application/json">(.*?)</script>', res.text)
        if match:
            data = json.loads(match.group(1))
            user_info = data.get("__DEFAULT_SCOPE__", {}).get("webapp.user-detail", {}).get("userInfo", {})
            user = user_info.get("user", {})
            stats = user_info.get("stats", {})
            
            return {
                "username": user.get("uniqueId", clean_username),
                "nickname": user.get("nickname", ""),
                "userId": user.get("id"),
                "avatarUrl": user.get("avatarLarger") or user.get("avatarMedium"),
                "bio": user.get("signature", ""),
                "isVerified": user.get("verified", False),
                "isPrivate": user.get("privateAccount", False),
                "followerCount": stats.get("followerCount", 0),
                "followingCount": stats.get("followingCount", 0),
                "likesCount": stats.get("heartCount", 0),
                "videoCount": stats.get("videoCount", 0)
            }
        
        raise HTTPException(status_code=422, detail="تعذر استخراج البيانات من الرد")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
```

---

## 3. إعداد مفاتيح API الخارجية بأمان

- يتم تخزين أي مفاتيح API خارجية في ملف `.env` عبر نظام **Secrets panel في Google AI Studio**.
- لا يتم تضمين أي مفاتيح سرية مشفرة بداخل ملف الـ APK النهائي لحمايتها من الهندسة العكسية.
- يتم حقن المفاتيح أثناء البناء عبر `BuildConfig` أو استهلاكها حصراً من طرف السيرفر الخلفي.

---

## 4. إعداد وبناء الـ APK عبر GitHub Actions

تم تجهيز ملف سير العمل `.github/workflows/build_apk.yml` تلقائياً، وهو يقوم بـ:
1. تشغيل الاختبارات الأوتوماتيكية (Unit Tests و Robolectric).
2. بناء كل من `assembleDebug` و `assembleRelease`.
3. رفع حزم APK كـ Artifacts جاهزة للتحميل الفوري على أجهزة أندرويد.

---

## 5. ميثاق الشفافية والمسؤولية

- تطبيق **TK_SABR** يلتزم بإظهار البيانات الحقيقية وتوضيح حالة عدم توفر أي مؤشر دون توليد أرقام عشوائية.
- النسبة المئوية محسوبة بناءً على خوارزمية موزونة للأركان الظاهرة (الملف الشخصي، التوازن، التفاعل، المحتوى، والموثوقية)، مع التنبيه بأنها تقييم تقديري لصحة الحساب وليست تدقيقاً أمنياً داخلياً لاختراق المنظومة.
