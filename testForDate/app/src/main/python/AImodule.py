import pickle
import numpy as np
from tensorflow import keras
from tensorflow.keras.preprocessing.sequence import pad_sequences
from os.path import dirname, join

def judge(content):
    # 匯入字典
    filename = join(dirname(__file__), "word_dict.pk")
    with open(filename, 'rb') as f:
        word_dictionary = pickle.load(f)
    filename = join(dirname(__file__), "label_dict.pk")
    with open(filename, 'rb') as f:
        output_dictionary = pickle.load(f)

    try:
        # 資料預處理
        input_shape = 180
        sent = content
        print("python content = " + content)
        if(len(content) == 0):
            return 2
        #content_str = str(content)
        #print("python content str = " + content_str)
        x = [[word_dictionary[word] for word in content]]
        x = pad_sequences(maxlen=input_shape, sequences=x, padding='post', value=0)

        # 載入模型
        model_save_path = join(dirname(__file__), "corpus_model.h5")
        lstm_model = keras.models.load_model(model_save_path, compile=False)

        # 模型預測
        y_predict = lstm_model.predict(x)

        label_dict = {v:k for k,v in output_dictionary.items()}
        result = label_dict[np.argmax(y_predict)] # 類型預測
        if result == "正面":
            print("預測正面")
            return 1
        elif result == "負面":
            print("預測負面")
            return 0

    except KeyError as err:
        print("預測失敗")
        print("不在詞彙表中的單詞為：%s." % err)
        return 2

#if __name__ == "__main__":
#    judge("今天久違地與同學聚會，有人推薦了一家專賣巧克力甜點的店，也許下次可以在那邊聚會。") # 需要回傳值所以做成函式來呼叫
