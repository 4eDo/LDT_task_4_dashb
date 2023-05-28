import json
import re
import numpy as np
import pandas as pd
import pickle

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.model_selection import GridSearchCV


stop_words = ['и', 'в', 'во', 'не', 'что', 'он', 'на', 'я', 'с', 'со', 'как', 'а', 'то', 'все', 'она', 'так', 'его', 'но', 'да', 'ты', 'к', 'у', 'же', 
'вы', 'за', 'бы', 'по', 'только', 'ее', 'мне', 'было', 'вот', 'от', 'меня', 'еще', 'нет', 'о', 'из', 'ему', 'теперь', 'когда', 'даже', 'ну', 'вдруг', 
'ли', 'если', 'уже', 'или', 'ни', 'быть', 'был', 'него', 'до', 'вас', 'нибудь', 'опять', 'уж', 'вам', 'ведь', 'там', 'потом', 'себя', 'ничего', 'ей', 
'может', 'они', 'тут', 'где', 'есть', 'надо', 'ней', 'для', 'мы', 'тебя', 'их', 'чем', 'была', 'сам', 'чтоб', 'без', 'будто', 'чего', 'раз', 'тоже', 
'себе', 'под', 'будет', 'ж', 'тогда', 'кто', 'этот', 'того', 'потому', 'этого', 'какой', 'совсем', 'ним', 'здесь', 'этом', 'один', 'почти', 'мой', 
'тем', 'чтобы', 'нее', 'сейчас', 'были', 'куда', 'зачем', 'всех', 'никогда', 'можно', 'при', 'наконец', 'два', 'об', 'другой', 'хоть', 'после', 'над', 
'больше', 'тот', 'через', 'эти', 'нас', 'про', 'всего', 'них', 'какая', 'много', 'разве', 'три', 'эту', 'моя', 'впрочем', 'хорошо', 'свою', 'этой', 
'перед', 'иногда', 'лучше', 'чуть', 'том', 'нельзя', 'такой', 'им', 'более', 'всегда', 'конечно', 'всю', 'между', 'которых', 'которые', 'твой', 
'которой', 'которого', 'сих', 'ком', 'свой', 'твоя', 'этими', 'слишком', 'нами', 'всему', 'будь', 'саму', 'чаще', 'ваше', 'сами', 'наш', 'затем', 
'самих', 'наши', 'ту', 'каждое', 'мочь', 'весь', 'этим', 'наша', 'своих', 'оба', 'который', 'зато', 'те', 'этих', 'вся', 'ваш', 'такая', 'теми', 
'ею', 'которая', 'нередко', 'каждая', 'также', 'чему', 'собой', 'самими', 'нем', 'вами', 'ими', 'откуда', 'такие', 'тому', 'та', 'очень', 'сама', 
'нему', 'алло', 'оно', 'этому', 'кому', 'тобой', 'таки', 'твоё', 'каждые', 'твои', 'нею', 'самим', 'ваши', 'ваша', 'кем', 'мои', 'однако', 'сразу', 
'свое', 'ними', 'всё', 'неё', 'тех', 'хотя', 'всем', 'тобою', 'тебе', 'одной', 'другие', 'само', 'эта', 'самой', 'моё', 'своей', 'такое', 'всею', 
'будут', 'своего', 'кого', 'свои', 'мог', 'нам', 'особенно', 'её', 'самому', 'наше', 'кроме', 'вообще', 'вон', 'мною', 'никто', 'это']

with open("main_vectorizer.pkl", "rb") as f:
    main_vectorizer = pickle.load(f)
with open("problem_vectorizer.pkl", "rb") as f:
    problem_vectorizer = pickle.load(f)
with open("model_main.pkl", "rb") as f:
    model_main = pickle.load(f)
with open("model_problem.pkl", "rb") as f:
    model_problem = pickle.load(f)
    


def prepare_rewiew(text):
    try:
        final = re.sub('[!|#|$|\%|&|@|[|\]|\_|.|,|\n|?]+', ' ', text).lower().strip()
        final = final if final else None
        return final 
    except AttributeError:
        return text.lower().strip() if text.lower().strip() else None

def process(json_data):

    df = pd.DataFrame(json_data).dropna()
    df['comment_fixed'] = df['comment'].apply(prepare_rewiew)
    df['comment_fixed']
    main_data = main_vectorizer.transform(df.comment_fixed)

    df['tone_predicted'] = model_main.predict(main_data)

    problem_df = df[df['tone_predicted']==3]

    problem_data = problem_vectorizer.transform(problem_df.comment_fixed)

    problem_df['problem_predicted'] = model_problem.predict(problem_data)

    final_data = df.join(problem_df[['id', 'problem_predicted']],lsuffix='_caller', rsuffix='_other')
    return final_data[['id_caller', 'tone_predicted', 'problem_predicted']].rename(columns = {'id_caller':'id'}).to_json(orient="records")