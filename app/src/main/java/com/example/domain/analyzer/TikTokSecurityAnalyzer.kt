package com.example.domain.analyzer

import com.example.data.model.AccountAuditResult
import com.example.data.model.AuditIssue
import com.example.data.model.AuditStrength
import com.example.data.model.IssueSeverity
import com.example.data.model.ScoreFactor
import com.example.data.model.TikTokAccount
import kotlin.math.roundToInt

object TikTokSecurityAnalyzer {

    fun analyze(account: TikTokAccount): AccountAuditResult {
        val issues = mutableListOf<AuditIssue>()
        val strengths = mutableListOf<AuditStrength>()
        val factors = mutableListOf<ScoreFactor>()

        var totalScore = 0

        // 1. Profile Completeness (Max 25 pts)
        var profilePts = 0
        val hasCustomAvatar = account.avatarUrl.isNotBlank() && !account.avatarUrl.contains("default-avatar")
        val hasBio = account.bio.isNotBlank()
        val hasNickname = account.nickname.isNotBlank() && account.nickname != account.username
        val hasOptimalUsername = account.username.length in 4..20 && !account.username.matches(Regex(".*[0-9]{6,}$"))

        if (hasCustomAvatar) profilePts += 8 else {
            issues.add(
                AuditIssue(
                    id = "missing_avatar",
                    severity = IssueSeverity.WARNING,
                    title = "عدم تعيين صورة شخصية مخصصة أو واضحة",
                    reason = "الحسابات التي تعتمد على الصورة الافتراضية تفتقر للهوية البصرية وتصنفها خوارزميات المنصة كحسابات غير نشطة أو وهمية.",
                    solution = "رفع صورة ملف شخصي عالية الدقة وواضحة المعالم لزيادة موثوقية الحساب لدى المتابعين والنظام."
                )
            )
        }

        if (hasBio) {
            profilePts += 7
            if (account.bio.length >= 20) profilePts += 3
        } else {
            issues.add(
                AuditIssue(
                    id = "empty_bio",
                    severity = IssueSeverity.WARNING,
                    title = "السيرة الذاتية (Bio) فارغة تماماً",
                    reason = "خلو خانة الوصف يحرم الحساب من الكلمات المفتاحية التوجيهية ويقلل ثقة الزوار الجدد.",
                    solution = "كتابة سيرة ذاتية موجزة توضح تخصص الحساب أو روابط التواصل والوسوم ذات الصلة."
                )
            )
        }

        if (hasNickname) profilePts += 4
        if (hasOptimalUsername) profilePts += 3 else {
            if (account.username.matches(Regex(".*[0-9]{6,}$"))) {
                issues.add(
                    AuditIssue(
                        id = "random_numbers_username",
                        severity = IssueSeverity.INFO,
                        title = "اسم المستخدم يحتوي على أرقام عشوائية متتالية",
                        reason = "وجود سلسلة أرقام طويلة مكررة في اسم المستخدم يعطي انطباعاً بأنه معرف تلقائي تم إنشاؤه بواسطة بوتات.",
                        solution = "تعديل اسم المستخدم (Username) إلى اسم تجاري أو شخصي واضح وسهل التذكر بدون أرقام زائدة."
                    )
                )
            }
        }
        profilePts = profilePts.coerceIn(0, 25)
        totalScore += profilePts
        val profileCompleteness = ((profilePts / 25.0) * 100).roundToInt()
        factors.add(
            ScoreFactor(
                title = "اكتمال الهوية والملف التعريفي",
                earnedPoints = profilePts,
                maxPoints = 25,
                description = "تقييم الصورة، الاسم المستعار، السيرة الذاتية وتناسق المعرف."
            )
        )

        // 2. Following / Follower Balance (Max 20 pts)
        var balancePts = 0
        val followers = account.followerCount
        val following = account.followingCount

        when {
            followers >= 1000 && following <= followers -> {
                balancePts = 20
                strengths.add(
                    AuditStrength(
                        id = "healthy_ratio",
                        title = "توازن صحي وممتاز بين المتابعين والمُتابَعين",
                        description = "الحساب يمتلك قاعدة جماهيرية حقيقية ولا يعتمد أسلوب المتابعة العشوائية لتحصيل الإضافات."
                    )
                )
            }
            followers > 0 && following > followers * 2 && following > 1000 -> {
                balancePts = 5
                issues.add(
                    AuditIssue(
                        id = "aggressive_following",
                        severity = IssueSeverity.WARNING,
                        title = "ارتفاع مفرط في عدد الحسابات التي يتابعها الحساب",
                        reason = "تجاوز عدد المتابَعين لعدد المتابعين بنسبة تزيد عن الضعف مؤشر شائع على أسلوب (Follow-for-Follow) الذي يقلل من جودة وتصنيف الحساب.",
                        solution = "تنظيف قائمة المتابَعين تدريجياً وإلغاء متابعة الحسابات غير النشطة للحفاظ على نسبة ظهور صحية."
                    )
                )
            }
            followers in 100..999 -> balancePts = 14
            followers < 100 && following < 100 -> balancePts = 10
            else -> balancePts = 12
        }
        balancePts = balancePts.coerceIn(0, 20)
        totalScore += balancePts
        factors.add(
            ScoreFactor(
                title = "تناسق نسبة المتابعين والمُتابَعين",
                earnedPoints = balancePts,
                maxPoints = 20,
                description = "مؤشر تدقيق التوازن لمنع أنماط التفاعل الوهمي والمتابعة العشوائية."
            )
        )

        // 3. Engagement and Likes-to-Followers Consistency (Max 25 pts)
        var engagementPts = 0
        val likes = account.likesCount
        val videos = account.videoCount
        val avgLikes = if (videos > 0) likes / videos else 0L

        val engagementRatio = if (followers > 0) {
            (likes.toDouble() / followers.toDouble())
        } else 0.0

        if (followers > 5000 && likes < 100) {
            engagementPts = 4
            issues.add(
                AuditIssue(
                    id = "ghost_followers",
                    severity = IssueSeverity.CRITICAL,
                    title = "انخفاض غير اعتيادي وحاد في مجموع الإعجابات مقارنة بعدد المتابعين",
                    reason = "امتلاك آلاف المتابعين مقابل عدد ضئيل جداً من الإعجابات يشير إما إلى متابعين غير حقيقيين (Ghost/Bot Followers) أو نفور الجمهور من المحتوى المنشور.",
                    solution = "إعادة تنشيط المتابعين عبر استطلاعات ومحتوى تفاعلي، وتجنب شراء المتابعين أو تبادل المتابعة غير الموجه."
                )
            )
        } else if (likes > 10000 && videos == 0L) {
            engagementPts = 8
            issues.add(
                AuditIssue(
                    id = "high_likes_zero_videos",
                    severity = IssueSeverity.INFO,
                    title = "وجود إعجابات مرتفعة مع عدم وجود فيديوهات معلنة",
                    reason = "قد يعني ذلك أن صاحب الحساب قام بحذف الفيديوهات أو تحويلها إلى الوضع الخاص (Private).",
                    solution = "نشر مقاطع جديدة بانتظام حتى تتمكن الخوارزمية من إعادة فهرسة تفاعل الجمهور الجديد."
                )
            )
        } else if (followers > 0 && likes > 0) {
            when {
                engagementRatio >= 2.0 -> {
                    engagementPts = 25
                    strengths.add(
                        AuditStrength(
                            id = "strong_engagement",
                            title = "معدل تفاعل جماهيري قوي وتراكمي",
                            description = "مجموع إعجابات الحساب يتجاوز ضعفي عدد المتابعين، وهو دليل ملموس على انتشار المقاطع ورواجها."
                        )
                    )
                }
                engagementRatio >= 0.5 -> {
                    engagementPts = 18
                    strengths.add(
                        AuditStrength(
                            id = "normal_engagement",
                            title = "تفاعل جماهيري طبيعي ومستقر",
                            description = "مؤشرات الإعجابات تتماشى مع المعدل الطبيعي للحسابات الصاعدة."
                        )
                    )
                }
                else -> {
                    engagementPts = 12
                }
            }
        } else {
            engagementPts = 10
        }
        engagementPts = engagementPts.coerceIn(0, 25)
        totalScore += engagementPts
        factors.add(
            ScoreFactor(
                title = "مؤشر التفاعل وتناسق الإعجابات",
                earnedPoints = engagementPts,
                maxPoints = 25,
                description = "فحص التناسب الرقمي بين المتابعين والإعجابات وعدد المقاطع."
            )
        )

        // 4. Content Volume & Activity Indicator (Max 15 pts)
        var contentPts = 0
        when {
            videos >= 20 -> {
                contentPts = 15
                strengths.add(
                    AuditStrength(
                        id = "active_content",
                        title = "سجل نشر غني ومتواصل",
                        description = "الحساب يحتوي على مكتبة مقاطع كافية لبناء حضور رقمي قوي وإثراء تجربة المشاهدين."
                    )
                )
            }
            videos in 5..19 -> {
                contentPts = 11
            }
            videos in 1..4 -> {
                contentPts = 6
                issues.add(
                    AuditIssue(
                        id = "low_video_volume",
                        severity = IssueSeverity.INFO,
                        title = "قلة عدد المقاطع المنشورة علناً",
                        reason = "نشر أقل من 5 فيديوهات يجعل تقييم تفاعل الحساب محدوداً ويؤخر انتشار الحساب في صفحة (لك / For You).",
                        solution = "الالتزام بجدول نشر أسبوعي مستمر (مقطعان إلى 4 مقاطع أسبوعياً) لتعزيز ظهور الحساب."
                    )
                )
            }
            else -> {
                contentPts = 2
                issues.add(
                    AuditIssue(
                        id = "zero_videos",
                        severity = IssueSeverity.WARNING,
                        title = "لا توجد أي مقاطع منشورة بشكل عام",
                        reason = "خلو الحساب من المقاطع العامة يجعله غير قابل للتحليل المتقدم ويعطله عن كسب جمهور حقيقي.",
                        solution = "البدء بنشر مقاطع قصيرة عالية الجودة متوافقة مع إرشادات المجتمع."
                    )
                )
            }
        }
        contentPts = contentPts.coerceIn(0, 15)
        totalScore += contentPts
        factors.add(
            ScoreFactor(
                title = "حجم المحتوى ونشاط النشر",
                earnedPoints = contentPts,
                maxPoints = 15,
                description = "قياس وفرة الفيديوهات العامة المتاحة للاستهلاك الجماهيري."
            )
        )

        // 5. Verification & Trust Signals (Max 15 pts)
        var trustPts = 0
        if (account.isVerified) {
            trustPts = 15
            strengths.add(
                AuditStrength(
                    id = "verified_badge",
                    title = "حساب موثق رسمي بالعلامة الزرقاء",
                    description = "الحساب معتمد وموثق رسمياً من TikTok كجهة أصلية أو شخصية عامة موثوقة."
                )
            )
        } else {
            // Unverified: calculate based on account age indicators or size
            if (followers > 50000) {
                trustPts = 12
            } else if (followers > 1000) {
                trustPts = 9
            } else {
                trustPts = 6
            }
        }

        if (account.isPrivate) {
            issues.add(
                AuditIssue(
                    id = "private_account",
                    severity = IssueSeverity.INFO,
                    title = "الحساب مضبوط على الوضع الخاص (Private)",
                    reason = "ضبط الحساب كخاص يمنع المشاهدين الجدد من الاطلاع على المحتوى قبل الموافقة، ويحد من انتشاره التلقائي.",
                    solution = "إذا كان الهدف بناء جمهور عام أو علامة تجارية، يُنصح بتغيير الحساب إلى (عام) من إعدادات الخصوصية."
                )
            )
        } else {
            strengths.add(
                AuditStrength(
                    id = "public_profile",
                    title = "الحساب عام ومتاح للجميع",
                    description = "سهولة وصول الجماهير للمحتوى تزيد من فرص الظهور في المقترحات والتريند."
                )
            )
        }

        trustPts = trustPts.coerceIn(0, 15)
        totalScore += trustPts
        factors.add(
            ScoreFactor(
                title = "مؤشرات الموثوقية والهوية",
                earnedPoints = trustPts,
                maxPoints = 15,
                description = "حالة التوثيق الرسمي ومستوى انفتاح الحساب للجمهور."
            )
        )

        // Final score bounded strictly between 0 and 100
        val finalScore = totalScore.coerceIn(0, 100)

        val ratingLevel = when (finalScore) {
            in 90..100 -> "ممتاز (صحة وموثوقية عالية)"
            in 70..89 -> "جيد (مؤشرات إيجابية مستقرة)"
            in 40..69 -> "متوسط (يحتاج إلى تحسينات هامة)"
            else -> "حرج (مؤشرات ضعيفة أو غير مكتملة)"
        }

        val engagementPercent = if (followers > 0) {
            ((likes.toDouble() / (followers * (if (videos > 0) videos else 1L)).toDouble()) * 100.0)
                .coerceAtMost(100.0)
        } else 0.0

        return AccountAuditResult(
            account = account,
            safetyScore = finalScore,
            ratingLevel = ratingLevel,
            issues = issues,
            strengths = strengths,
            factors = factors,
            engagementRatePercent = engagementPercent,
            avgLikesPerVideo = avgLikes,
            profileCompletenessPercent = profileCompleteness
        )
    }
}
