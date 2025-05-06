package com.winjay.practice.performance_optimize;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.winjay.practice.R;
import com.winjay.practice.common.BaseActivity;

/**
 * 性能优化学习
 *
 * @author Winjay
 * @date 2021-09-09
 */
public class PerformanceOptimizeActivity extends BaseActivity {
    ExpandableListView elv;

    public String[] groupStrings = {
            "布局优化(1.删除布局中无用的控件和层级 2.有选择的使用性能较差的ViewGroup，比如RelativeLayout)",
            "绘制优化(View的onDraw方法要避免执行大量的操作)",
            "内存泄漏优化(1.开发过程中避免写出有内存泄漏的代码 2.通过分析工具来找出潜在的内存泄漏)",
            "响应速度优化(避免在主线程执行耗时操作，耗时操作放到子线程中)",
            "ListView优化",
            "Bitmap优化",
            "线程优化(采用线程池，避免程序中存在大量的Thread)",
            "一些性能优化建议"
    };
    public String[][] childStrings = {
            {
                    "1.<include>标签",
                    "2.<merge>标签(配合<include>标签一起使用)",
                    "3.ViewStub"
            },
            {
                    "1.onDraw方法中不要创建新的局部对象",
                    "2.onDraw方法中不要做耗时任务，也不能执行成千上万次的循环操作"
            },
            {
                    "1.静态变量导致的内存泄漏(1.static context=this 2.static view=new View(this))",
                    "2.单例模式导致的内存泄漏(Singleton.getInstance().registerListener(this)却没有unregisterListener)",
                    "3.属性动画导致的内存泄漏(animator.setRepeatCount(ValueAnimator.INFINITE) animator.start()且没有animator.cancel()时)"
            },
            {
                    "1.Activity如果5s内无法响应屏幕触摸事件或者键盘输入事件就会ANR",
                    "2.BroadcastReceiver如果10s内还未执行完操作也会出现ANR",
                    "3.ANR文件traces.txt的存放位置为/data/anr目录"
            },
            {
                    "1.采用ViewHolder并避免在getView中执行耗时操作",
                    "2.根据列表滑动状态控制任务执行频率，滑动不加载，停止滑动继续加载",
                    "3.尝试开启硬件加速使其更加流畅"
            },
            {
                    "1.通过BitmapFactory.Options来根据需要对图片进行采样，主要使用BitmapFactory.Options的inSampleSize参数"
            },
            {
                    "1.线程池可以重用内部的线程，从而避免了线程的创建和销毁所带来的的性能开销",
                    "2.线程池还能很有效的控制线程池的最大并发数，避免大量的线程因互相抢占系统资源从而导致阻塞现象的发生"
            },
            {
                    "1.避免创建过多的对象",
                    "2.不要过多的使用枚举，枚举占用的内存空间要比整形大",
                    "3.常量请使用 static final 来修饰",
                    "4.使用一些Android特有的数据结构，比如SparseArray和Pair等，它们都具有更高的性能",
                    "5.适当使用软引用和弱引用",
                    "6.采用内存缓存和磁盘缓存",
                    "7.尽量采用静态内部类，这样可以避免潜在的由于内部类而导致的内存泄漏"
            }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.performance_optimize_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        elv = findViewById(R.id.elv);
        elv.setAdapter(new MyAdapter());
        // 设置分组项的点击监听事件
        elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
//                toast(groupStrings[i]);
                // 请务必返回 false，否则分组不会展开
                return false;
            }
        });
        // 设置子选项点击监听事件
        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                toast(childStrings[groupPosition][childPosition]);
                return true;
            }
        });
    }

    private class MyAdapter extends BaseExpandableListAdapter {

        // 获取分组的个数
        @Override
        public int getGroupCount() {
            return groupStrings.length;
        }

        // 获取指定分组中的子选项的个数
        @Override
        public int getChildrenCount(int groupPosition) {
            return childStrings[groupPosition].length;
        }

        // 获取指定的分组数据
        @Override
        public Object getGroup(int groupPosition) {
            return groupStrings[groupPosition];
        }

        // 获取指定分组中的指定子选项数据
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childStrings[groupPosition][childPosition];
        }

        // 获取指定分组的ID, 这个ID必须是唯一的
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        // 获取子选项的ID, 这个ID必须是唯一的
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        // 分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们。
        @Override
        public boolean hasStableIds() {
            return true;
        }

        // 获取显示指定分组的视图
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder groupViewHolder;
            if (convertView == null) {
                TextView textView = new TextView(PerformanceOptimizeActivity.this);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                textView.setTextColor(getResources().getColor(R.color.black_color));
                convertView = textView;
                groupViewHolder = new GroupViewHolder();
                groupViewHolder.tvTitle = textView;
                convertView.setTag(groupViewHolder);
            } else {
                groupViewHolder = (GroupViewHolder) convertView.getTag();
            }
            groupViewHolder.tvTitle.setText(groupStrings[groupPosition]);
            return convertView;
        }

        // 获取显示指定分组中的指定子选项的视图
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder childViewHolder;
            if (convertView == null) {
                TextView textView = new TextView(PerformanceOptimizeActivity.this);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                textView.setTextColor(Color.GREEN);
                convertView = textView;
                childViewHolder = new ChildViewHolder();
                childViewHolder.tvTitle = textView;
                convertView.setTag(childViewHolder);
            } else {
                childViewHolder = (ChildViewHolder) convertView.getTag();
            }
            childViewHolder.tvTitle.setText("  " + childStrings[groupPosition][childPosition]);
            return convertView;
        }

        // 指定位置上的子元素是否可选中
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private static class GroupViewHolder {
        TextView tvTitle;
    }

    private static class ChildViewHolder {
        TextView tvTitle;
    }
}
