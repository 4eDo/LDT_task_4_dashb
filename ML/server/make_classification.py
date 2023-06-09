import json
import re, unicodedata
import numpy as np
import pandas as pd
import pickle
import nltk
nltk.data.path.append("/")

from num2words import num2words

from ufal.udpipe import Model, Pipeline
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.model_selection import GridSearchCV


def remove_non_ascii(words):
    """Удаление символов, не являющихся символами ASCII, из списка токенизированных слов"""
    new_words = []
    for word in words:
        new_word = unicodedata.normalize('NFKD', word)
        new_words.append(new_word)
    return new_words
def to_lowercase(words):
    """Преобразование всех символов в строчные из списка токенизированных слов"""
    new_words = []
    for word in words:
        new_word = word.lower()
        new_words.append(new_word)
    return new_words
def remove_punctuation(words):
    """Удаление знаков препинания из списка токенизированных слов"""
    new_words = []
    for word in words:
        new_word = re.sub(r'[^\w\s]', '', word)
        if new_word != '':
            new_words.append(new_word)
    return new_words
def replace_numbers(words):
    """Заменить все числа в списке токенизированных слов текстовым представлением"""
    new_words = []
    for word in words:
        if word.isdigit():
            new_word = num2words(word, lang='ru')
            new_words.append(new_word)
        else:
            new_words.append(word)
    return new_words
def remove_stopwords(words):
    """Удаление стоп-слов из списка токенизированных слов"""
    new_words = []
    for word in words:
        if word not in stop_words:
            new_words.append(word)
    return new_words
def stem_words(words):
    """Исходные слова в списке токенизированных слов"""
    stemmer = LancasterStemmer()
    stems = []
    for word in words:
        stem = stemmer.stem(word)
        stems.append(stem)
    return stems
def lemmatize_verbs(words):
    """Лемматизировать глаголы в списке токенизированных слов"""
    lemmatizer = WordNetLemmatizer()
    lemmas = []
    for word in words:
        lemma = lemmatizer.lemmatize(word, pos='v')
        lemmas.append(lemma)
    return lemmas
def normalize(words):
    words = remove_non_ascii(words)
    words = to_lowercase(words)
    words = remove_punctuation(words)
    words = replace_numbers(words)
    words = remove_stopwords(words)
    return words
def normilized_null(text = 'Example'):
    text = ' '.join(normalize(nltk.word_tokenize(text)))
    return text if text else None

stop_words = ['и', 'в', 'во', 'что', 'он', 'на', 'я', 'с', 'со', 'как', 'а', 'то', 'все', 'она', 'так', 'его', 'но', 'да', 'ты', 'к', 'у', 'же', 
'вы', 'за', 'бы', 'по', 'только', 'ее', 'мне', 'было', 'вот', 'от', 'меня', 'еще', 'нет', 'о', 'из', 'ему', 'теперь', 'когда', 'даже', 'ну', 'вдруг', 
'ли', 'если', 'уже', 'или', 'быть', 'него', 'до', 'вас', 'нибудь', 'опять', 'уж', 'вам', 'ведь', 'там', 'потом', 'себя', 'ей', 
'может', 'они', 'тут', 'где', 'есть', 'надо', 'ней', 'для', 'мы', 'тебя', 'их', 'была', 'сам', 'чтоб', 'без', 'будто', 'чего', 'тоже', 
'себе', 'под', 'будет', 'ж', 'тогда', 'кто', 'этот', 'того', 'потому', 'этого', 'какой', 'совсем', 'ним', 'здесь', 'этом', 'почти', 'мой', 
'тем', 'чтобы', 'нее', 'сейчас', 'были', 'куда', 'зачем', 'всех', 'никогда', 'можно', 'при', 'наконец', 'об', 'другой', 'хоть', 'после', 'над'
, 'тот', 'через', 'эти', 'нас', 'про', 'них', 'какая', 'много', 'разве', 'три', 'эту', 'моя', 'впрочем', 'свою', 'этой', 
'перед', 'иногда', 'чуть', 'том', 'нельзя', 'такой', 'им', 'более', 'всегда', 'конечно', 'всю', 'между', 'которых', 'которые', 'твой', 
'которой', 'которого', 'сих', 'ком', 'свой', 'твоя', 'этими', 'нами', 'всему', 'будь', 'саму', 'чаще', 'ваше', 'сами', 'наш', 'затем', 
'самих', 'наши', 'ту', 'каждое', 'мочь', 'весь', 'этим', 'наша', 'своих', 'оба', 'который', 'зато', 'те', 'этих', 'вся', 'ваш', 'такая', 'теми', 
'ею', 'которая', 'нередко', 'каждая', 'также', 'чему', 'собой', 'самими', 'нем', 'вами', 'ими', 'откуда', 'такие', 'тому', 'та', 'сама', 
'нему', 'алло', 'оно', 'этому', 'кому', 'тобой', 'таки', 'твоё', 'каждые', 'твои', 'нею', 'самим', 'ваши', 'ваша', 'кем', 'мои', 'однако', 
'свое', 'ними', 'всё', 'неё', 'тех', 'хотя', 'всем', 'тобою', 'тебе', 'одной', 'само', 'эта', 'самой', 'моё', 'своей', 'такое', 'всею', 
'будут', 'своего', 'кого', 'свои', 'мог', 'нам', 'особенно', 'её', 'самому', 'наше', 'кроме', 'вообще', 'вон', 'мною', 'никто', 'это']

with open("main_vectorizer_1.pkl", "rb") as f:
    main_vectorizer = pickle.load(f)
with open("model_main_1.pkl", "rb") as f:
    model_main = pickle.load(f)
with open("model_problem_1.pkl", "rb") as f:
    model_problem = pickle.load(f)
with open("model_category_gor.pkl", "rb") as f:
    model_category_gor = pickle.load(f)
with open("model_category_dost.pkl", "rb") as f:
    model_category_dost = pickle.load(f)
with open("model_category_post.pkl", "rb") as f:
    model_category_post = pickle.load(f)
with open("model_category_tovar.pkl", "rb") as f:
    model_category_tovar = pickle.load(f)
with open("model_category_upak.pkl", "rb") as f:
    model_category_upak = pickle.load(f)
    
def process(pipeline, text='Строка', keep_pos=True, keep_punct=False):
    entities = {'PROPN'}
    named = False  # переменная для запоминания того, что нам встретилось имя собственное
    memory = []
    mem_case = None
    mem_number = None
    tagged_propn = []

   # обрабатываем текст, получаем результат в формате conllu:
    processed = pipeline.process(text)
    
   # пропускаем строки со служебной информацией:
    content = [l for l in processed.split('\n') if not l.startswith('#')]

   # извлекаем из обработанного текста леммы, тэги и морфологические характеристики
    tagged = [w.split('\t') for w in content if w]

    for t in tagged:
        if len(t) != 10: # если список короткий — строчка не содержит разбора, пропускаем
            continue
        (word_id,token,lemma,pos,xpos,feats,head,deprel,deps,misc) = t 
        if not lemma or not token: # если слово пустое — пропускаем
            continue
        if pos in entities: # здесь отдельно обрабатываем имена собственные — они требуют особого обращения
            if '|' not in feats:
                tagged_propn.append('%s_%s' % (lemma, pos))
                continue
            morph = {el.split('=')[0]: el.split('=')[1] for el in feats.split('|')}
            if 'Case' not in morph or 'Number' not in morph:
                tagged_propn.append('%s_%s' % (lemma, pos))
                continue
            if not named:
                named = True
                mem_case = morph['Case']
                mem_number = morph['Number']
            if morph['Case'] == mem_case and morph['Number'] == mem_number:
                memory.append(lemma)
                if 'SpacesAfter=\\n' in misc or 'SpacesAfter=\s\\n' in misc:
                    named = False
                    past_lemma = '::'.join(memory)
                    memory = []
                    tagged_propn.append(past_lemma + '_PROPN ')
            else:
                named = False
                past_lemma = '::'.join(memory)
                memory = []
                tagged_propn.append(past_lemma + '_PROPN ')
                tagged_propn.append('%s_%s' % (lemma, pos))
        else:
            if not named:
                if pos == 'NUM' and token.isdigit():  # Заменяем числа на xxxxx той же длины
                    lemma = num_replace(token)
                tagged_propn.append('%s_%s' % (lemma, pos))
            else:
                named = False
                past_lemma = '::'.join(memory)
                memory = []
                tagged_propn.append(past_lemma + '_PROPN ')
                tagged_propn.append('%s_%s' % (lemma, pos))

    if not keep_punct: # обрабатываем случай, когда пользователь попросил не сохранять пунктуацию (по умолчанию она сохраняется)
        tagged_propn = [word for word in tagged_propn if word.split('_')[1] != 'PUNCT']
    if not keep_pos:
        tagged_propn = [word.split('_')[0] for word in tagged_propn]
    return tagged_propn



modelfile = 'udpipe_syntagrus.model'
model_token = Model.load(modelfile)
process_pipeline = Pipeline(model_token, 'tokenize', Pipeline.DEFAULT, Pipeline.DEFAULT, 'conllu')


def tokenize(text):
    if text:
        return ' '.join(process(process_pipeline, text=text, ))
    else: return None

def prepare_comment(text):
    if text:
        return tokenize(normilized_null(text))
    else: return None

def predict_category(comment, problem):
    if not comment:
        return None
    vectorized_comment = main_vectorizer.transform([comment])
    if problem == 0:
        return float(model_category_gor.predict(vectorized_comment)[0])
    if problem == 1:
        return float(model_category_dost.predict(vectorized_comment)[0])
    if problem == 2:
        return float(model_category_tovar.predict(vectorized_comment)[0])
    elif problem == 4:
        return float(model_category_post.predict(vectorized_comment)[0])
    elif problem == 5:
        return float(model_category_upak.predict(vectorized_comment)[0])
    else: return None

def process_data(json_data):

    df = pd.DataFrame(json_data).dropna()
    df['comment_fixed'] = df['comment'].apply(prepare_comment)
    df_fixed = df.dropna()
    if len(df_fixed.comment_fixed) == 0:
        raise ValueError
    main_data = main_vectorizer.transform(df_fixed.comment_fixed)
    df_fixed['tone_predicted'] = model_main.predict(main_data)
    
    df = df.merge(df_fixed[['id', 'tone_predicted']],on='id', how='left')

    problem_df = df[df['tone_predicted']==3].dropna()
    if len(problem_df.comment_fixed) > 0:
        problem_data = main_vectorizer.transform(problem_df.comment_fixed)
        problem_df['problem_predicted'] = model_problem.predict(problem_data)
    else: problem_df['problem_predicted'] = None

    df = df.merge(problem_df[['id', 'problem_predicted']],on='id', how='left')
    df['category_predicted'] = df.apply(lambda x: predict_category(x.comment_fixed, x.problem_predicted), axis=1)
    return df[['id', 'tone_predicted', 'problem_predicted','category_predicted']].to_json(orient="records")
