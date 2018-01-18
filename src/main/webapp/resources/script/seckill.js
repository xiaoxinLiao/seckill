/*
*   存放主要的交互逻辑js代码
* */
var seckill = {
    //封装秒杀相关ajax的url
    URL: {
        now: function () {
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        execution: function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' +md5 +'/execute';
        }
    },

    countDown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        if (nowTime > endTime) {
            seckillBox.html('秒杀结束');
        } else if (nowTime < startTime) {
            //秒杀未开启
            var killTime = new Date(Number(startTime) + 1000);
            seckillBox.countdown(killTime, function (event) {
                var format = event.strftime('秒杀计时: %D天 %H小时 %M分 %S秒');
                seckillBox.html(format);

            }).on('finish.countdown', function () {
                //获取秒杀地址,控制实现逻辑,执行秒杀
                seckill.handleSeckill(seckillId, seckillBox);
            });
        } else {
            //秒杀开始
            seckill.handleSeckill(seckillId, seckillBox);
        }
    },
    //执行秒杀
    handleSeckill: function (seckillId, node) {
        node.hide()
            .html('<button class="btn btn-primary btn-lg " id="killBtn">' +
                '开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            //在回调函数中执行交互逻辑
            if (result && result['success']) {
                var exposer = result['data'];
                if(exposer['exposed']){
                    //开启秒杀
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId,md5);
                    console.log("url:" + killUrl);
                    //绑定一次点击事件
                    $('#killBtn').one('click',function () {
                        //执行秒杀请求
                        //1.禁用按钮
                        $(this).addClass("disabled");
                        //发送秒杀请求
                        $.post(killUrl,{},function (result) {
                            if(result && result['success']){
                                //成功
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                //秒杀显示秒杀结果
                                node.html('<span class="label label-success">'
                                    + stateInfo +'</span>');
                            }else{
                                //秒杀失败
                                var error = result['error'];
                                //显示秒杀结果
                                node.html('<span class="label label-danger">'
                                    + error +'</span>');
                            }
                        });
                    });
                    node.show();

                }else{
                    //秒杀未开启
                    //客户机时间超前
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    //重新开始计时逻辑
                    seckill.countDown(seckillId,now,start,end);
                }
            } else {
                console.log("result:",result);
            }
        });
    },
    //验证手机号
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        }
        return false;
    },
    //详情页秒杀逻辑初始化
    detail: {
        init: function (params) {
            //手机验证和登录，计时交互
            //在cookie中查找手机号

            var killPhone = $.cookie('killPhone');
            if (!seckill.validatePhone(killPhone)) {
                //绑定手机号
                //弹出层
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true, //显示弹出层
                    backdrop: 'static', //禁止位置关闭
                    keyboard: false, //关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var killPhone = $('#killphoneKey').val();
                    if (seckill.validatePhone(killPhone)) {
                        //刷新页面
                        $.cookie('killPhone', killPhone, {expires: 7, path: '/seckill'});
                        window.location.reload();
                    } else {
                        $('#killphoneMessage').hide()
                            .html('<label class="label label-danger">手机号错误</label>')
                            .show(500);
                    }
                })
            }
            //计时
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];

            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    //计时交互
                    seckill.countDown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log("result=" + result);
                }
            })
        }
    }
}